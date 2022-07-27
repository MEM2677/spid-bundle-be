package com.entando.spid;

import com.entando.spid.domain.Organization;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import static com.entando.spid.Constants.KEYCLOAK_ENTANDO_DISPLAY_NAME;
import static com.entando.spid.Constants.KEYCLOAK_NEW_AUTH_FLOW_NAME;

public class ConfigUtils {


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
        // TODO eseguire controllo entità pubblica o privata
        return organization;
    }


    public static JSONObject configureIdp(Organization organization, String template) throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();
        String org = objectMapper.writeValueAsString(organization);
        JSONObject json = new JSONObject(template);
        JSONObject data = new JSONObject(org);
        return configureIdp(data, json, true);
    }


    public static JSONObject configureIdp(String organization, String template) throws JSONException {
        JSONObject data = new JSONObject(organization);
        JSONObject json = new JSONObject(template);
        return configureIdp(data, json, true);
    }

    public static JSONObject configureIdp(JSONObject organizationData, JSONObject template, boolean overwrite) throws JSONException {
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

}
