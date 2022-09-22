package com.entando.spid;

import com.entando.spid.domain.Organization;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import static com.entando.spid.Constants.*;

public class ConfigUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

    @Deprecated
    public static Organization getOrganization() {
        Map<String, String> envVars = System.getenv();
        Organization organization = new Organization();

        organization.setOrganizationNames(envVars.get("ORGANIZATION_NAMES"));
        organization.setOrganizationDisplayNames(envVars.get("ORGANIZATION_DISPLAY_NAMES"));
        organization.setOrganizationUrls(envVars.get("ORGANIZATION_URLS"));
        organization.setOtherContactCompany(envVars.get("OTHER_CONTACT_COMPANY"));
        organization.setOtherContactPhone(envVars.get("OTHER_CONTACT_PHONE"));
        organization.setOtherContactEmail(envVars.get("OTHER_CONTACT_EMAIL"));
        organization.setOtherContactVatNumber(envVars.get("OTHER_CONTACT_VAT_NUMBER"));
        organization.setOtherContactFiscalCode(envVars.get("OTHER_CONTACT_FISCAL_CODE"));
        organization.setOtherContactIsSpPrivate(envVars.get("OTHER_CONTACT_IS_SP_PRIVATE"));
        organization.setOtherContactIpaCode(envVars.get("OTHER_CONTACT_IPA_CODE"));
        organization.setBillingContactCompany(envVars.get("BILLING_CONTACT_COMPANY"));
        organization.setBillingContactPhone(envVars.get("BILLING_CONTACT_PHONE"));
        organization.setBillingContactEmail(envVars.get("BILLING_CONTACT_EMAIL"));
        organization.setBillingContactRegistryName(envVars.get("BILLING_CONTACT_REGISTRY_NAME"));
        organization.setBillingContactSiteAddress(envVars.get("BILLING_CONTACT_SITE_ADDRESS"));
        organization.setBillingContactSiteNumber(envVars.get("BILLING_CONTACT_SITE_NUMBER"));
        organization.setBillingContactSiteCity(envVars.get("BILLING_CONTACT_SITE_CITY"));
        organization.setBillingContactSiteZipCode(envVars.get("BILLING_CONTACT_SITE_ZIP_CODE"));
        organization.setBillingContactSiteProvince(envVars.get("BILLING_CONTACT_SITE_PROVINCE"));
        organization.setBillingContactSiteCountry(envVars.get("BILLING_CONTACT_SITE_COUNTRY"));
        return organization;
    }


    public static JSONObject configureIdentityProvider(Organization organization, String template) throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();
        String org = objectMapper.writeValueAsString(organization);
        JSONObject json = new JSONObject(template);
        JSONObject data = new JSONObject(org);
        return configureIdentityProvider(data, json, true);
    }


    public static JSONObject configureIdentityProvider(String organization, String template) throws JSONException {
        JSONObject data = new JSONObject(organization);
        JSONObject json = new JSONObject(template);
        return configureIdentityProvider(data, json, true);
    }

    public static JSONObject configureIdentityProvider(JSONObject organizationData, JSONObject template, boolean overwrite) throws JSONException {
        JSONObject config = template.getJSONObject("config");

        // copy the organization fields
        Iterator<String> itr = organizationData.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            config.put(key, organizationData.get(key));
        }
        // impose the remaining config fields
        updateField(config,"attributeConsumingServiceIndex", "1", true);
        updateField(config,"attributeConsumingServiceName", KEYCLOAK_ENTANDO_DISPLAY_NAME, overwrite);
        updateField(config,"authnContextClassRefs", "[\"https://www.spid.gov.it/SpidL1\"]", overwrite);

        updateField(config,"authnContextComparisonType", "minimum", true);
        updateField(config,"forceAuthn", "true", true);
        updateField(config,"signSpMetadata", "true", true);
        updateField(config,"syncMode", "FORCE", true);
        updateField(config,"wantAssertionsSigned", "true", true);
        updateField(config,"xmlSigKeyInfoKeyNameTransformer", "NONE", true);
        // impose the general fields
        template.remove("internalId");
        template.put("firstBrokerLoginFlowAlias", KEYCLOAK_NEW_AUTH_FLOW_NAME);
        template.put("trustEmail", true);
        // finally
        template.put("config", config);
        return template;
    }

    protected static void updateField(JSONObject json, String key, Object value, boolean overwrite) throws JSONException {
        if (json != null && StringUtils.isNotBlank(key)) {
            if (overwrite || !json.has(key)) {
                json.put(key, value);
            }
        }
    }

    /**
     * Get the home directory for IO
     * @return
     */
    public static String getHomeDirectory() {
        String userHome = "user.home";
        String path = System.getProperty(userHome);
        return path;
    }

    /**
     * Get the local path where to store organization properties
     * @return
     */
    public static Path getOrganizationFilePath() {
        String home = ConfigUtils.getHomeDirectory();
        Path path = Paths.get(home + File.separator + LOCAL_ORGANIZATION_FILE);
        return path;
    }


    /**
     * Get the local path where to store organization properties
     * @return
     */
    public static Path getProviderFilePath() {
        String home = ConfigUtils.getHomeDirectory();
        Path path = Paths.get(home + File.separator + LOCAL_PROVIDER_FILE);
        return path;
    }

    /**
     *
     * @param path the path of the text file to save
     * @param data
     * @return
     */
    public static boolean writeFile(Path path, String data) {
        try {
            // delete existing file
            if (Files.deleteIfExists(path)) {
                logger.debug("Deleting existing file {}", path);
            }
            // write
            Files.writeString(path, data, StandardCharsets.UTF_8);
            logger.info("Wrote configuration in {}", path);
            return true;
        } catch (Throwable t) {
            logger.error("Error writing file " + path, t);
        }
        return false;
    }

    /**
     * Read the content of a config text file
     * @param path the path
     * @return the content of the text file
     * @throws Throwable in case of any error
     */
    public static String readFile(Path path) throws Throwable {
        StringBuilder sb = new StringBuilder();

        if (path != null && Files.exists(path)) {
            File file = path.toFile();
            String in;

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while ((in = br.readLine()) != null) {
                sb.append(in);
                sb.append(System.getProperty("line.separator"));
            }
        } else {
            logger.error("cannot read file {}", path);
        }
        return sb.toString();
    }


}
