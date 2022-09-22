package com.entando.spid.service.impl;

import com.entando.spid.ConfigUtils;
import com.entando.spid.domain.Template;
import com.entando.spid.service.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.entando.spid.Constants.RESOURCE_TEMPLATES_PATH;

/**
 * Service Implementation for managing {@link Template}.
 */
@Service
public class TemplateServiceImpl implements TemplateService {


    private final Logger log = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private final Map<String, Template> templates = new ConcurrentHashMap<>();

    public TemplateServiceImpl() {
        try {
            // prepare the map containing the templates
            prepareConfigurationMap(RESOURCE_TEMPLATES_PATH);
            // if this is the first run we create the local file that mirrors
            // the configuration from the default configuration, otherwise we load
            // the templates from the file
            Path path = ConfigUtils.getProviderFilePath();

            if (Files.exists(path)) {
                log.info("Loading templates from file {}", path);
                String json = ConfigUtils.readFile(path);
                importTemplates(json);
            } else {
                log.info("Saving templates to file {}", path);
                if (!synchronizeTemplates()) {
                    throw new RuntimeException("error in local file creation");
                }
            }
        } catch (Throwable t) {
            templates.clear();
            log.error("Error on service startup ", t);
        }
    }

    /**
     * Synchronize template map with local file
     *
     * @return true only if the operation is successful
     */
    private boolean synchronizeTemplates() {
        Path path = ConfigUtils.getProviderFilePath();
        String export = exportTemplates();
        return ConfigUtils.writeFile(path, export);
    }

    @Override
    public List<Template> getTemplates() {
        return templates.values()
            .stream()
            .collect(Collectors.toList());
    }

    @Override
    public boolean updateTemplate(Template template) {
        if (template != null
            && StringUtils.isNotBlank(template.getName())) {
            templates.put(template.getName(), template);
            log.debug("added or updated template '{}'", template.getName());
            // update template file
            return synchronizeTemplates();
        } else {
            log.warn("cannot update template!");
        }
        return false;
    }

    @Override
    public String exportTemplates() {
        ObjectMapper objectMapper = new ObjectMapper();
        Template[] out = new Template[templates.size()];
        int idx = 0;
        String json = "";

        try {
            for (Template template: templates.values()) {
                out[idx++] = template;
            }
            json = objectMapper.writeValueAsString(out);
        } catch (Throwable t) {
            log.error("error exporting template", t);
            json = null;
        }
        return json;
    }

    @Override
    public boolean importTemplates(String in) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Template[] templates = objectMapper.readValue(in, Template[].class);
            return importTemplates(templates);
        } catch (Throwable t) {
            log.error("error importing templates", t);
        }
        return false;
    }

    @Override
    public boolean importTemplates(Template[] templates) {
        boolean updated = false;

        try {
            if (templates != null) {
                this.templates.clear();
                for (Template current: templates) {
                    this.templates.put(current.getName(), current);
                }
                updated = true;
            }
        } catch (Throwable t) {
            log.error("error importing template", t);
        }
        return updated;
    }

    @Override
    public void prepareConfigurationMap(String fileName) {
        InputStream inputStream = getInputStream(fileName);

        // create configuration map from static file
        try (InputStreamReader streamReader =
                 new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                // ignore empty lines
                if (StringUtils.isBlank(line)) {
                    continue;
                }
//                log.debug("Input configuration line: {}", line);
                // convert to IdP
                String[] token = line.split(";");
                Template config = new Template();

                if (token.length != 2) {
                    throw new RuntimeException("Unexpected CSV format! Expected 2 semicolons ';' as delimiter");
                }

                config.setName(token[0]);
                config.setConfig(token[1]);

                templates.put(token[0], config);
                log.debug("Template for provider {} successfully imported", config.getName());
            }
            log.info("{} providers configuration imported", templates.size());
        } catch (IOException t) {
            throw new RuntimeException("Error loading configuration file!", t);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("Error closing the stream", e);
            }
        }
    }

    private InputStream getInputStream(String fileName) {
        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("Configuration file not found! " + fileName);
        }
        return inputStream;
    }

}
