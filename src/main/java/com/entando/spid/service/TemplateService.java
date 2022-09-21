package com.entando.spid.service;

import com.entando.spid.domain.Template;

import java.util.List;

/**
 * Service Interface for managing {@link Template}.
 */
public interface TemplateService {

    /**
     * Return templates for identity providers
     * @return
     */
    List<Template> getTemplates();

    /**
     * Update the template of a single provider
     * @param template the template to update, identified by the name
     */
    void updateTemplate(Template template);

    /**
     * Export provider templates
     * @return the JSON of the templates as an array of objects
     */
    String exportTemplates();

    /**
     * Import provider templates
     *
     * @param in the JSON of the providers returned by export function
     * @return true if the configuration was updated, false otherwise
     */
    boolean importTemplates(String in);

    /**
     * Import provider templates
     *
     * @param templates the themplates to import
     * @return true if the configuration was updated, false otherwise
     */
    boolean importTemplates(Template[] templates);

    /**
     * Read a file from the resources as inputStream
     *
     * @param fileName the path of teh file to read
     */
  void prepareConfigurationMap(String fileName);

}
