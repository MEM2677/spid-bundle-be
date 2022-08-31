package com.entando.spid.domain;

import com.entando.spid.ConfigUtils;
import com.entando.spid.SpidApp;
import com.entando.spid.domain.keycloak.Client;
import com.entando.spid.web.rest.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ContextConfiguration(classes = SpidApp.class, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
@AutoConfigureMockMvc
class IdpTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Idp.class);
        Idp idp1 = new Idp();
        idp1.setId(1L);
        Idp idp2 = new Idp();
        idp2.setId(idp1.getId());
        assertThat(idp1).isEqualTo(idp2);
        idp2.setId(2L);
        assertThat(idp1).isNotEqualTo(idp2);
        idp1.setId(null);
        assertThat(idp1).isNotEqualTo(idp2);
    }


    @Test
    public void getAppName() {
        Map<String, String> env = getEnvMap();
        Optional<String> appName = env
            .keySet()
            .stream()
            .filter(k -> k.endsWith("_CM_SERVICE_PORT_8083_TCP_ADDR"))
            .findFirst()
            .map(k -> k.replaceFirst("_CM_SERVICE_PORT_8083_TCP_ADDR", "").toLowerCase());
        Assertions.assertTrue(appName.isPresent());
        Assertions.assertEquals("spid", appName.get());
    }

    @Test
    public void testClient() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Client client = objectMapper.readValue(CLIENT_JSON, Client.class);
        Assertions.assertNotNull(client);
        Assertions.assertEquals("bcc5b6c3-784a-4e8b-bc7c-da5f6a890bcc", client.getId());
        Assertions.assertEquals("account", client.getClientId());
    }

    @Test
    public void testConfiguration() throws JSONException {
        /*
        JSONObject json = new JSONObject(CONFIG_JSON).getJSONObject("config");
        JSONObject jsonComplete = new JSONObject(CONFIG_COMPLETE_JSON).getJSONObject("config");
        List<String> keys = new ArrayList<>();
        List<String> keysComplete = new ArrayList<>();

        Iterator<String> itr = json.keys();
        while (itr.hasNext()) keys.add(itr.next());
        itr = jsonComplete.keys();
        while (itr.hasNext()) keysComplete.add(itr.next());

        for (String key : keysComplete) {
            if (!keys.contains(key)) {
                System.out.println(">>> " + key);
            }
        }
        */

        JSONObject organization = new JSONObject(ORGANIZATION_DATA_JSON);
        JSONObject json = new JSONObject(CONFIG_JSON);

//        System.out.println(">OLD> " + json);
        json = ConfigUtils.configureIdp(organization, json, false);
//        System.out.println(">CUR> " + json);

    }

    protected Map<String, String> getEnvMap() {
        Map<String, String> map = new HashMap<>();

        map.put("SPID_AB_SERVICE_PORT_8081_TCP", "tcp://10.103.158.194:8081");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP_ADDR", "10.102.209.177");
        map.put("SPID_AB_SERVICE_PORT_8081_TCP_PROTO", "tcp");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP", "tcp://10.102.209.177:8083");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP_PROTO", "tcp");
        map.put("SPID_CM_SERVICE_SERVICE_PORT_DE_PORT", "8083");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP_PORT", "8083");
        map.put("SPID_AB_SERVICE_PORT_8081_TCP_PORT", "8081");
        map.put("SPID_AB_SERVICE_SERVICE_PORT", "8081");
        map.put("SPID_CM_SERVICE_SERVICE_PORT", "8083");
        map.put("SPID_AB_SERVICE_PORT", "tcp://10.103.158.194:8081");
        map.put("SPID_AB_SERVICE_PORT_8081_TCP_ADDR", "10.103.158.194");
        map.put("SPID_CM_SERVICE_PORT", "tcp://10.102.209.177:8083");
        map.put("SPID_AB_SERVICE_SERVICE_PORT_APPBUILDER_PORT", "8081");
        map.put("SPID_CM_SERVICE_SERVICE_HOST", "10.102.209.177");
        map.put("SPID_AB_SERVICE_SERVICE_HOST", "10.103.158.194");
        return map;
    }

    public final static String CONFIG_JSON = "{\"alias\": \"spid-test-base\",\"displayName\": \"SPID TEST base\",\"internalId\": \"163caa06-ee61-4ee4-b42e-4e636e9b3829\",\"providerId\": \"spid\",\"enabled\": true,\"updateProfileFirstLoginMode\": \"on\",\"trustEmail\": false,\"storeToken\": false,\"addReadTokenRoleOnCreate\": false,\"authenticateByDefault\": false,\"linkOnly\": false,\"firstBrokerLoginFlowAlias\": \"first broker login\",\"config\": {\"validateSignature\": \"true\",\"signingCertificate\": \"MIIEGDCCAwCgAwIBAgIJAOrYj9oLEJCwMA0GCSqGSIb3DQEBCwUAMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDAeFw0xOTA0MTExMDAyMDhaFw0yNTAzMDgxMDAyMDhaMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK8kJVo+ugRrbbv9xhXCuVrqi4B7/MQzQc62ocwlFFujJNd4m1mXkUHFbgvwhRkQqo2DAmFeHiwCkJT3K1eeXIFhNFFroEzGPzONyekLpjNvmYIs1CFvirGOj0bkEiGaKEs+/umzGjxIhy5JQlqXE96y1+Izp2QhJimDK0/KNij8I1bzxseP0Ygc4SFveKS+7QO+PrLzWklEWGMs4DM5Zc3VRK7g4LWPWZhKdImC1rnS+/lEmHSvHisdVp/DJtbSrZwSYTRvTTz5IZDSq4kAzrDfpj16h7b3t3nFGc8UoY2Ro4tRZ3ahJ2r3b79yK6C5phY7CAANuW3gDdhVjiBNYs0CAwEAAaOByjCBxzAdBgNVHQ4EFgQU3/7kV2tbdFtphbSA4LH7+w8SkcwwgZcGA1UdIwSBjzCBjIAU3/7kV2tbdFtphbSA4LH7+w8SkcyhaaRnMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdIIJAOrYj9oLEJCwMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAJNFqXg/V3aimJKUmUaqmQEEoSc3qvXFITvT5f5bKw9yk/NVhR6wndL+z/24h1OdRqs76blgH8k116qWNkkDtt0AlSjQOx5qvFYh1UviOjNdRI4WkYONSw+vuavcx+fB6O5JDHNmMhMySKTnmRqTkyhjrch7zaFIWUSV7hsBuxpqmrWDoLWdXbV3eFH3mINA5AoIY/m0bZtzZ7YNgiFWzxQgekpxd0vcTseMnCcXnsAlctdir0FoCZztxMuZjlBjwLTtM6Ry3/48LMM8Z+lw7NMciKLLTGQyU8XmKKSSOh0dGh5Lrlt5GxIIJkH81C0YimWebz8464QPL3RbLnTKg+c=\",\"postBindingLogout\": \"true\",\"singleLogoutServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\"postBindingResponse\": \"true\",\"nameIDPolicyFormat\": \"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\",\"principalAttribute\": \"fiscalNumber\",\"entityId\": \"https://forumpa.apps.psdemo.eng-entando.com/auth/realms/entando\",\"xmlSigKeyInfoKeyNameTransformer\": \"KEY_ID\",\"signatureAlgorithm\": \"RSA_SHA256\",\"idpEntityId\": \"https://demo.spid.gov.it\",\"useJwksUrl\": \"true\",\"loginHint\": \"false\",\"allowCreate\": \"true\",\"syncMode\": \"IMPORT\",\"authnContextComparisonType\": \"minimum\",\"postBindingAuthnRequest\": \"true\",\"singleSignOnServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\"wantAuthnRequestsSigned\": \"true\",\"addExtensionsElementWithKeyInfo\": \"false\",\"principalType\": \"ATTRIBUTE\"}}";
    public final static String CONFIG_JSON_PA = "{\"alias\": \"spid-pubblica-amministrazione\",\"displayName\": \"SPID Pubblica Amministrazione\",\"providerId\": \"spid\",\"enabled\": true,\"updateProfileFirstLoginMode\": \"on\",\"trustEmail\": false,\"storeToken\": false,\"addReadTokenRoleOnCreate\": false,\"authenticateByDefault\": false,\"linkOnly\": false,\"firstBrokerLoginFlowAlias\": \"first broker login\",\"config\": {\"postBindingLogout\": \"true\",\"otherContactPhone\": \"+395556935632\",\"postBindingResponse\": \"true\",\"singleLogoutServiceUrl\": \"https://identity.sieltecloud.it/simplesaml/saml2/idp/SLS.php\",\"organizationDisplayNames\": \"en|Organization, display it|Organizzazione display\",\"organizationUrls\": \"it|https://forumpa.apps.psdemo.eng-entando.com/pa, en|https://forumpa.apps.psdemo.eng-entando.com/pa\",\"otherContactEmail\": \"bastachesia@gmail.com\",\"xmlSigKeyInfoKeyNameTransformer\": \"KEY_ID\",\"idpEntityId\": \"https://identity.sieltecloud.it\",\"loginHint\": \"false\",\"allowCreate\": \"true\",\"organizationNames\": \"en|Organization, it|Organizzazione\",\"syncMode\": \"FORCE\",\"authnContextComparisonType\": \"minimum\",\"wantAuthnRequestsSigned\": \"true\",\"singleSignOnServiceUrl\": \"https://identity.sieltecloud.it/simplesaml/saml2/idp/SSO.php\",\"encryptionPublicKey\": \"MIIDczCCAlugAwIBAgIJAMsX0iEKQM6xMA0GCSqGSIb3DQEBCwUAMFAxCzAJBgNVBAYTAklUMQ4wDAYDVQQIDAVJdGFseTEgMB4GA1UEBwwXU2FuIEdyZWdvcmlvIGRpIENhdGFuaWExDzANBgNVBAoMBlNpZWx0ZTAeFw0xNTEyMTQwODE0MTVaFw0yNTEyMTMwODE0MTVaMFAxCzAJBgNVBAYTAklUMQ4wDAYDVQQIDAVJdGFseTEgMB4GA1UEBwwXU2FuIEdyZWdvcmlvIGRpIENhdGFuaWExDzANBgNVBAoMBlNpZWx0ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANIRlOjM/tS9V9jYjJreqZSctuYriLfPTDgX2XdhWEbMpMpwA9p0bsbLQoC1gP0piLO+qbCsIh9+boPfb4/dLIA7E+Vmm5/+evOtzvjfHG4oXjZK6jo08QwkVV8Bm1jkakJPVZ57QFbyDSr+uBbIMY7CjA2LdgnIIwKN/kSfFhrZUMJ6ZxwegM100X5psfNPSV9WUtgHsvqlIlvydPo2rMm21sg+2d3Vtg8DthNSYRLqgazCc0NTsigrH7niSbJCO0nq/svMX2rSFdh5GFK7/pxT+c3OFWqIR8r+RX4qW+auJqkbTuNRwxV22Sm6r69ZJwV0WspvsVJi+FYqiyoWhgUCAwEAAaNQME4wHQYDVR0OBBYEFCUx063GwUhEFDllwCBe/+jdeW+XMB8GA1UdIwQYMBaAFCUx063GwUhEFDllwCBe/+jdeW+XMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBADF94c3JwyBM86QBLeoUZxRYKPniba8B39FfJk0pb+LejKfZMvspOrOFgYQQ9UrS8IFkBX9Xr7/tjRbr2cPwZNjrEZhoq+NfcE09bnaWTyEl1IEKK8TWOupJj9UNVpYXX0LfIRrMwNEzAPQykOaqPOnyHxOCPTY957xXSo3jXOyvugtvPHbd+iliAzUoPm1tgiTKWS+EkQ/e22eFv5NEyT+oHiKovrQ+voPWOIvJVMjiTyxRic8fEnI9zzV0SxWvFvty77wgcYbeEuFZa3iidhojUge8o1uY/JUyQjFxcvvfAgWSIZwdHiNyWaAgwzLPmPCPsvBdR3xrlcDg/9Bd3D0=\",\"validateSignature\": \"true\",\"signingCertificate\": \"MIIDczCCAlugAwIBAgIJAMsX0iEKQM6xMA0GCSqGSIb3DQEBCwUAMFAxCzAJBgNVBAYTAklUMQ4wDAYDVQQIDAVJdGFseTEgMB4GA1UEBwwXU2FuIEdyZWdvcmlvIGRpIENhdGFuaWExDzANBgNVBAoMBlNpZWx0ZTAeFw0xNTEyMTQwODE0MTVaFw0yNTEyMTMwODE0MTVaMFAxCzAJBgNVBAYTAklUMQ4wDAYDVQQIDAVJdGFseTEgMB4GA1UEBwwXU2FuIEdyZWdvcmlvIGRpIENhdGFuaWExDzANBgNVBAoMBlNpZWx0ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANIRlOjM/tS9V9jYjJreqZSctuYriLfPTDgX2XdhWEbMpMpwA9p0bsbLQoC1gP0piLO+qbCsIh9+boPfb4/dLIA7E+Vmm5/+evOtzvjfHG4oXjZK6jo08QwkVV8Bm1jkakJPVZ57QFbyDSr+uBbIMY7CjA2LdgnIIwKN/kSfFhrZUMJ6ZxwegM100X5psfNPSV9WUtgHsvqlIlvydPo2rMm21sg+2d3Vtg8DthNSYRLqgazCc0NTsigrH7niSbJCO0nq/svMX2rSFdh5GFK7/pxT+c3OFWqIR8r+RX4qW+auJqkbTuNRwxV22Sm6r69ZJwV0WspvsVJi+FYqiyoWhgUCAwEAAaNQME4wHQYDVR0OBBYEFCUx063GwUhEFDllwCBe/+jdeW+XMB8GA1UdIwQYMBaAFCUx063GwUhEFDllwCBe/+jdeW+XMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBADF94c3JwyBM86QBLeoUZxRYKPniba8B39FfJk0pb+LejKfZMvspOrOFgYQQ9UrS8IFkBX9Xr7/tjRbr2cPwZNjrEZhoq+NfcE09bnaWTyEl1IEKK8TWOupJj9UNVpYXX0LfIRrMwNEzAPQykOaqPOnyHxOCPTY957xXSo3jXOyvugtvPHbd+iliAzUoPm1tgiTKWS+EkQ/e22eFv5NEyT+oHiKovrQ+voPWOIvJVMjiTyxRic8fEnI9zzV0SxWvFvty77wgcYbeEuFZa3iidhojUge8o1uY/JUyQjFxcvvfAgWSIZwdHiNyWaAgwzLPmPCPsvBdR3xrlcDg/9Bd3D0=\",\"nameIDPolicyFormat\": \"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\",\"principalAttribute\": \"fiscalNumber\",\"entityId\": \"https://forumpa.apps.psdemo.eng-entando.com/auth/realms/entando\",\"signatureAlgorithm\": \"RSA_SHA256\",\"otherContactCompany\": \"Entando1\",\"useJwksUrl\": \"true\",\"otherContactIpaCode\": \"IPA123\",\"postBindingAuthnRequest\": \"true\",\"addExtensionsElementWithKeyInfo\": \"false\",\"principalType\": \"ATTRIBUTE\"}}";
    public final static String CONFIG_COMPLETE_JSON = "{\"alias\": \"spid-test-pubblico\",\"displayName\": \"SPID TEST Login\",\"internalId\": \"a3174814-0e1d-4962-a4b1-2f4f5680c808\",\"providerId\": \"spid\",\"enabled\": true,\"updateProfileFirstLoginMode\": \"on\",\"trustEmail\": true,\"storeToken\": false,\"addReadTokenRoleOnCreate\": false,\"authenticateByDefault\": false,\"linkOnly\": false,\"firstBrokerLoginFlowAlias\": \"SPID first broker login\",\"config\": {\"authnContextClassRefs\": \"[\\\"https://www.spid.gov.it/SpidL1\\\"]\",\"otherContactPhone\": \"+395556935632\",\"postBindingLogout\": \"true\",\"postBindingResponse\": \"true\",\"singleLogoutServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\"organizationDisplayNames\": \"en|Entando, it|Entando\",\"billingContactSiteCity\": \"CA\",\"debugEnabled\": \"true\",\"organizationUrls\": \"it|https://forumpa.apps.psdemo.eng-entando.com/entando-de-app/, en|https://forumpa.apps.psdemo.eng-entando.com/entando-de-app/\",\"otherContactEmail\": \"bastachesia@gmail.com\",\"xmlSigKeyInfoKeyNameTransformer\": \"NONE\",\"idpEntityId\": \"https://demo.spid.gov.it\",\"loginHint\": \"false\",\"allowCreate\": \"true\",\"organizationNames\": \"en|Entando, it|Entando\",\"billingContactSiteAddress\": \"Piazza Salento\",\"authnContextComparisonType\": \"minimum\",\"syncMode\": \"FORCE\",\"billingContactCompany\": \"Entando\",\"singleSignOnServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\"wantAuthnRequestsSigned\": \"true\",\"validateSignature\": \"true\",\"billingContactRegistryName\": \"Entando\",\"signingCertificate\": \"MIIEGDCCAwCgAwIBAgIJAOrYj9oLEJCwMA0GCSqGSIb3DQEBCwUAMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDAeFw0xOTA0MTExMDAyMDhaFw0yNTAzMDgxMDAyMDhaMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK8kJVo+ugRrbbv9xhXCuVrqi4B7/MQzQc62ocwlFFujJNd4m1mXkUHFbgvwhRkQqo2DAmFeHiwCkJT3K1eeXIFhNFFroEzGPzONyekLpjNvmYIs1CFvirGOj0bkEiGaKEs+/umzGjxIhy5JQlqXE96y1+Izp2QhJimDK0/KNij8I1bzxseP0Ygc4SFveKS+7QO+PrLzWklEWGMs4DM5Zc3VRK7g4LWPWZhKdImC1rnS+/lEmHSvHisdVp/DJtbSrZwSYTRvTTz5IZDSq4kAzrDfpj16h7b3t3nFGc8UoY2Ro4tRZ3ahJ2r3b79yK6C5phY7CAANuW3gDdhVjiBNYs0CAwEAAaOByjCBxzAdBgNVHQ4EFgQU3/7kV2tbdFtphbSA4LH7+w8SkcwwgZcGA1UdIwSBjzCBjIAU3/7kV2tbdFtphbSA4LH7+w8SkcyhaaRnMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdIIJAOrYj9oLEJCwMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAJNFqXg/V3aimJKUmUaqmQEEoSc3qvXFITvT5f5bKw9yk/NVhR6wndL+z/24h1OdRqs76blgH8k116qWNkkDtt0AlSjQOx5qvFYh1UviOjNdRI4WkYONSw+vuavcx+fB6O5JDHNmMhMySKTnmRqTkyhjrch7zaFIWUSV7hsBuxpqmrWDoLWdXbV3eFH3mINA5AoIY/m0bZtzZ7YNgiFWzxQgekpxd0vcTseMnCcXnsAlctdir0FoCZztxMuZjlBjwLTtM6Ry3/48LMM8Z+lw7NMciKLLTGQyU8XmKKSSOh0dGh5Lrlt5GxIIJkH81C0YimWebz8464QPL3RbLnTKg+c=\",\"principalAttribute\": \"fiscalNumber\",\"nameIDPolicyFormat\": \"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\",\"billingContactSiteNumber\": \"9\",\"billingContactPhone\": \"+395556935632\",\"entityId\": \"https://forumpa.apps.psdemo.eng-entando.com/auth/realms/entando\",\"attributeConsumingServiceName\": \"Login SPID TEST\",\"signSpMetadata\": \"true\",\"otherContactCompany\": \"Entando\",\"signatureAlgorithm\": \"RSA_SHA256\",\"useJwksUrl\": \"true\",\"wantAssertionsSigned\": \"true\",\"otherContactVatNumber\": \"IT03264290929\",\"otherContactIsSpPrivate\": \"true\",\"otherContactIpaCode\": \"92028890926\",\"billingContactSiteCountry\": \"IT\",\"billingContactSiteZipCode\": \"09127\",\"postBindingAuthnRequest\": \"true\",\"forceAuthn\": \"true\",\"attributeConsumingServiceIndex\": \"1\",\"billingContactSiteProvince\": \"Cagliari\",\"addExtensionsElementWithKeyInfo\": \"false\",\"billingContactEmail\": \"bastachesia@gmail.com\",\"principalType\": \"ATTRIBUTE\"}}";
    public final static String CONFIG_COMPLETE_JSON_PA = "{\"alias\": \"spid-pubblica-amministrazione\",\"displayName\": \"SPID Pubblica Amministrazione\",\"internalId\": \"358c2567-5ab9-45b9-a1cf-9d4fa44aa5f2\",\"providerId\": \"spid\",\"enabled\": false,\"updateProfileFirstLoginMode\": \"on\",\"trustEmail\": true,\"storeToken\": false,\"addReadTokenRoleOnCreate\": false,\"authenticateByDefault\": false,\"linkOnly\": false,\"firstBrokerLoginFlowAlias\": \"SPID first broker login\",\"config\": {\"postBindingLogout\": \"true\",\"otherContactPhone\": \"+395556935632\",\"authnContextClassRefs\": \"[\\\"https://www.spid.gov.it/SpidL1\\\"]\",\"postBindingResponse\": \"true\",\"singleLogoutServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\"organizationDisplayNames\": \"en|Organization display, it|Organizzazione display\",\"debugEnabled\": \"true\",\"organizationUrls\": \"it|https://forumpa.apps.psdemo.eng-entando.com/pa, en|https://forumpa.apps.psdemo.eng-entando.com/pa\",\"otherContactEmail\": \"bastachesia@gmail.com\",\"xmlSigKeyInfoKeyNameTransformer\": \"NONE\",\"idpEntityId\": \"https://demo.spid.gov.it\",\"loginHint\": \"false\",\"allowCreate\": \"true\",\"organizationNames\": \"en|Organization, it|Organizzazione\",\"syncMode\": \"IMPORT\",\"authnContextComparisonType\": \"minimum\",\"wantAuthnRequestsSigned\": \"true\",\"singleSignOnServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\"validateSignature\": \"true\",\"signingCertificate\": \"MIIEGDCCAwCgAwIBAgIJAOrYj9oLEJCwMA0GCSqGSIb3DQEBCwUAMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDAeFw0xOTA0MTExMDAyMDhaFw0yNTAzMDgxMDAyMDhaMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK8kJVo+ugRrbbv9xhXCuVrqi4B7/MQzQc62ocwlFFujJNd4m1mXkUHFbgvwhRkQqo2DAmFeHiwCkJT3K1eeXIFhNFFroEzGPzONyekLpjNvmYIs1CFvirGOj0bkEiGaKEs+/umzGjxIhy5JQlqXE96y1+Izp2QhJimDK0/KNij8I1bzxseP0Ygc4SFveKS+7QO+PrLzWklEWGMs4DM5Zc3VRK7g4LWPWZhKdImC1rnS+/lEmHSvHisdVp/DJtbSrZwSYTRvTTz5IZDSq4kAzrDfpj16h7b3t3nFGc8UoY2Ro4tRZ3ahJ2r3b79yK6C5phY7CAANuW3gDdhVjiBNYs0CAwEAAaOByjCBxzAdBgNVHQ4EFgQU3/7kV2tbdFtphbSA4LH7+w8SkcwwgZcGA1UdIwSBjzCBjIAU3/7kV2tbdFtphbSA4LH7+w8SkcyhaaRnMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdIIJAOrYj9oLEJCwMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAJNFqXg/V3aimJKUmUaqmQEEoSc3qvXFITvT5f5bKw9yk/NVhR6wndL+z/24h1OdRqs76blgH8k116qWNkkDtt0AlSjQOx5qvFYh1UviOjNdRI4WkYONSw+vuavcx+fB6O5JDHNmMhMySKTnmRqTkyhjrch7zaFIWUSV7hsBuxpqmrWDoLWdXbV3eFH3mINA5AoIY/m0bZtzZ7YNgiFWzxQgekpxd0vcTseMnCcXnsAlctdir0FoCZztxMuZjlBjwLTtM6Ry3/48LMM8Z+lw7NMciKLLTGQyU8XmKKSSOh0dGh5Lrlt5GxIIJkH81C0YimWebz8464QPL3RbLnTKg+c=\",\"nameIDPolicyFormat\": \"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\",\"principalAttribute\": \"fiscalNumber\",\"entityId\": \"https://forumpa.apps.psdemo.eng-entando.com/auth/realms/entando\",\"attributeConsumingServiceName\": \"Login SPID TEST\",\"signSpMetadata\": \"true\",\"signatureAlgorithm\": \"RSA_SHA256\",\"otherContactCompany\": \"Entando\",\"useJwksUrl\": \"true\",\"wantAssertionsSigned\": \"true\",\"otherContactIpaCode\": \"IPA123\",\"postBindingAuthnRequest\": \"true\",\"forceAuthn\": \"true\",\"attributeConsumingServiceIndex\": \"1\",\"addExtensionsElementWithKeyInfo\": \"false\",\"principalType\": \"ATTRIBUTE\"}}";
    public final static String ORGANIZATION_DATA_JSON = "{\"organizationNames\": \"en|Entando, it|Entando\",\"organizationDisplayNames\": \"en|Entando, it|Entando\",\"organizationUrls\": \"it|https://forumpa.apps.psdemo.eng-entando.com/entando-de-app/, en|https://forumpa.apps.psdemo.eng-entando.com/entando-de-app/\",\"otherContactCompany\": \"Entando\", \"otherContactPhone\": \"+395556935632\",\"otherContactEmail\": \"bastachesia@gmail.com\",\"otherContactVatNumber\": \"IT03264290929\",\"otherContactFiscalCode\": \"FISCAL\",\"billingContactCompany\": \"Entando\",\"billingContactPhone\": \"+395556935632\",\"billingContactEmail\": \"bastachesia@gmail.com\",\"billingContactRegistryName\": \"Entando\",\"billingContactSiteAddress\": \"Piazza Salento\",\"billingContactSiteNumber\": \"9\",\"billingContactSiteCity\": \"CA\",\"billingContactSiteZipCode\": \"09127\",\"billingContactSiteProvince\": \"Cagliari\",\"billingContactSiteCountry\": \"IT\", \"otherContactIsSpPrivate\": \"true\"}";

    public final static String TEST_CONFIG_BASE_JSON = "{{alias\": \"spid-test-base\",displayName\": \"SPID TEST base\",providerId\": \"spid\",enabled\": true,updateProfileFirstLoginMode\": \"on\",trustEmail\": false,storeToken\": false,addReadTokenRoleOnCreate\": false,authenticateByDefault\": false,linkOnly\": false,firstBrokerLoginFlowAlias\": \"first broker login\",config\": {validateSignature\": \"true\",signingCertificate\": \"MIIEGDCCAwCgAwIBAgIJAOrYj9oLEJCwMA0GCSqGSIb3DQEBCwUAMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDAeFw0xOTA0MTExMDAyMDhaFw0yNTAzMDgxMDAyMDhaMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK8kJVo+ugRrbbv9xhXCuVrqi4B7/MQzQc62ocwlFFujJNd4m1mXkUHFbgvwhRkQqo2DAmFeHiwCkJT3K1eeXIFhNFFroEzGPzONyekLpjNvmYIs1CFvirGOj0bkEiGaKEs+/umzGjxIhy5JQlqXE96y1+Izp2QhJimDK0/KNij8I1bzxseP0Ygc4SFveKS+7QO+PrLzWklEWGMs4DM5Zc3VRK7g4LWPWZhKdImC1rnS+/lEmHSvHisdVp/DJtbSrZwSYTRvTTz5IZDSq4kAzrDfpj16h7b3t3nFGc8UoY2Ro4tRZ3ahJ2r3b79yK6C5phY7CAANuW3gDdhVjiBNYs0CAwEAAaOByjCBxzAdBgNVHQ4EFgQU3/7kV2tbdFtphbSA4LH7+w8SkcwwgZcGA1UdIwSBjzCBjIAU3/7kV2tbdFtphbSA4LH7+w8SkcyhaaRnMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdIIJAOrYj9oLEJCwMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAJNFqXg/V3aimJKUmUaqmQEEoSc3qvXFITvT5f5bKw9yk/NVhR6wndL+z/24h1OdRqs76blgH8k116qWNkkDtt0AlSjQOx5qvFYh1UviOjNdRI4WkYONSw+vuavcx+fB6O5JDHNmMhMySKTnmRqTkyhjrch7zaFIWUSV7hsBuxpqmrWDoLWdXbV3eFH3mINA5AoIY/m0bZtzZ7YNgiFWzxQgekpxd0vcTseMnCcXnsAlctdir0FoCZztxMuZjlBjwLTtM6Ry3/48LMM8Z+lw7NMciKLLTGQyU8XmKKSSOh0dGh5Lrlt5GxIIJkH81C0YimWebz8464QPL3RbLnTKg+c=\",postBindingLogout\": \"true\",singleLogoutServiceUrl\": \"https://demo.spid.gov.it/samlsso\",postBindingResponse\": \"true\",nameIDPolicyFormat\": \"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\",principalAttribute\": \"fiscalNumber\",entityId\": \"https://forumpa.apps.psdemo.eng-entando.com/auth/realms/entando\",xmlSigKeyInfoKeyNameTransformer\": \"KEY_ID\",signatureAlgorithm\": \"RSA_SHA256\",idpEntityId\": \"https://demo.spid.gov.it\",useJwksUrl\": \"true\",loginHint\": \"false\",allowCreate\": \"true\",syncMode\": \"IMPORT\",authnContextComparisonType\": \"exact\",postBindingAuthnRequest\": \"true\",singleSignOnServiceUrl\": \"https://demo.spid.gov.it/samlsso\",wantAuthnRequestsSigned\": \"true\",addExtensionsElementWithKeyInfo\": \"false\",principalType\": \"ATTRIBUTE\"}}";

    public final static String TEST_CONFIGURED_BASE_JSON = "{\n" +
        "        \"alias\": \"spid-test-pubblico\",\n" +
        "        \"displayName\": \"SPID TEST Login\",\n" +
        "        \"providerId\": \"spid\",\n" +
        "        \"enabled\": true,\n" +
        "        \"updateProfileFirstLoginMode\": \"on\",\n" +
        "        \"trustEmail\": true,\n" +
        "        \"storeToken\": false,\n" +
        "        \"addReadTokenRoleOnCreate\": false,\n" +
        "        \"authenticateByDefault\": false,\n" +
        "        \"linkOnly\": false,\n" +
        "        \"firstBrokerLoginFlowAlias\": \"SPID first broker login\",\n" +
        "        \"config\": {\n" +
        "            \"authnContextClassRefs\": \"[\\\"https://www.spid.gov.it/SpidL1\\\"]\",\n" +
        "            \"otherContactPhone\": \"+395556935632\",\n" +
        "            \"postBindingLogout\": \"true\",\n" +
        "            \"postBindingResponse\": \"true\",\n" +
        "            \"singleLogoutServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\n" +
        "            \"organizationDisplayNames\": \"en|Entando, it|Entando\",\n" +
        "            \"billingContactSiteCity\": \"CA\",\n" +
        "            \"debugEnabled\": \"true\",\n" +
        "            \"organizationUrls\": \"it|https://forumpa.apps.psdemo.eng-entando.com/entando-de-app/, en|https://forumpa.apps.psdemo.eng-entando.com/entando-de-app/\",\n" +
        "            \"otherContactEmail\": \"bastachesia@gmail.com\",\n" +
        "            \"xmlSigKeyInfoKeyNameTransformer\": \"NONE\",\n" +
        "            \"idpEntityId\": \"https://demo.spid.gov.it\",\n" +
        "            \"loginHint\": \"false\",\n" +
        "            \"allowCreate\": \"true\",\n" +
        "            \"organizationNames\": \"en|Entando, it|Entando\",\n" +
        "            \"billingContactSiteAddress\": \"Piazza Salento\",\n" +
        "            \"authnContextComparisonType\": \"minimum\",\n" +
        "            \"syncMode\": \"FORCE\",\n" +
        "            \"billingContactCompany\": \"Entando\",\n" +
        "            \"singleSignOnServiceUrl\": \"https://demo.spid.gov.it/samlsso\",\n" +
        "            \"wantAuthnRequestsSigned\": \"true\",\n" +
        "            \"validateSignature\": \"true\",\n" +
        "            \"billingContactRegistryName\": \"Entando\",\n" +
        "            \"signingCertificate\": \"MIIEGDCCAwCgAwIBAgIJAOrYj9oLEJCwMA0GCSqGSIb3DQEBCwUAMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDAeFw0xOTA0MTExMDAyMDhaFw0yNTAzMDgxMDAyMDhaMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK8kJVo+ugRrbbv9xhXCuVrqi4B7/MQzQc62ocwlFFujJNd4m1mXkUHFbgvwhRkQqo2DAmFeHiwCkJT3K1eeXIFhNFFroEzGPzONyekLpjNvmYIs1CFvirGOj0bkEiGaKEs+/umzGjxIhy5JQlqXE96y1+Izp2QhJimDK0/KNij8I1bzxseP0Ygc4SFveKS+7QO+PrLzWklEWGMs4DM5Zc3VRK7g4LWPWZhKdImC1rnS+/lEmHSvHisdVp/DJtbSrZwSYTRvTTz5IZDSq4kAzrDfpj16h7b3t3nFGc8UoY2Ro4tRZ3ahJ2r3b79yK6C5phY7CAANuW3gDdhVjiBNYs0CAwEAAaOByjCBxzAdBgNVHQ4EFgQU3/7kV2tbdFtphbSA4LH7+w8SkcwwgZcGA1UdIwSBjzCBjIAU3/7kV2tbdFtphbSA4LH7+w8SkcyhaaRnMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdIIJAOrYj9oLEJCwMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAJNFqXg/V3aimJKUmUaqmQEEoSc3qvXFITvT5f5bKw9yk/NVhR6wndL+z/24h1OdRqs76blgH8k116qWNkkDtt0AlSjQOx5qvFYh1UviOjNdRI4WkYONSw+vuavcx+fB6O5JDHNmMhMySKTnmRqTkyhjrch7zaFIWUSV7hsBuxpqmrWDoLWdXbV3eFH3mINA5AoIY/m0bZtzZ7YNgiFWzxQgekpxd0vcTseMnCcXnsAlctdir0FoCZztxMuZjlBjwLTtM6Ry3/48LMM8Z+lw7NMciKLLTGQyU8XmKKSSOh0dGh5Lrlt5GxIIJkH81C0YimWebz8464QPL3RbLnTKg+c=\",\n" +
        "            \"principalAttribute\": \"fiscalNumber\",\n" +
        "            \"nameIDPolicyFormat\": \"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\",\n" +
        "            \"billingContactSiteNumber\": \"9\",\n" +
        "            \"billingContactPhone\": \"+395556935632\",\n" +
        "            \"entityId\": \"https://forumpa.apps.psdemo.eng-entando.com/auth/realms/entando\",\n" +
        "            \"attributeConsumingServiceName\": \"Login SPID TEST\",\n" +
        "            \"signSpMetadata\": \"true\",\n" +
        "            \"otherContactCompany\": \"Entando\",\n" +
        "            \"signatureAlgorithm\": \"RSA_SHA256\",\n" +
        "            \"useJwksUrl\": \"true\",\n" +
        "            \"wantAssertionsSigned\": \"true\",\n" +
        "            \"otherContactVatNumber\": \"IT03264290929\",\n" +
        "            \"otherContactIsSpPrivate\": \"true\",\n" +
        "            \"otherContactIpaCode\": \"92028890926\",\n" +
        "            \"billingContactSiteCountry\": \"IT\",\n" +
        "            \"billingContactSiteZipCode\": \"09127\",\n" +
        "            \"postBindingAuthnRequest\": \"true\",\n" +
        "            \"forceAuthn\": \"true\",\n" +
        "            \"attributeConsumingServiceIndex\": \"1\",\n" +
        "            \"billingContactSiteProvince\": \"Cagliari\",\n" +
        "            \"addExtensionsElementWithKeyInfo\": \"false\",\n" +
        "            \"billingContactEmail\": \"bastachesia@gmail.com\",\n" +
        "            \"principalType\": \"ATTRIBUTE\"\n" +
        "        }\n" +
        "    }";

    public final static String CLIENT_JSON = "{\n" +
        "\"id\": \"bcc5b6c3-784a-4e8b-bc7c-da5f6a890bcc\",\n" +
        "\"clientId\": \"account\",\n" +
        "\"name\": \"${client_account}\",\n" +
        "\"rootUrl\": \"${authBaseUrl}\",\n" +
        "\"baseUrl\": \"/realms/entando/account/\",\n" +
        "\"surrogateAuthRequired\": false,\n" +
        "\"enabled\": true,\n" +
        "\"alwaysDisplayInConsole\": false,\n" +
        "\"clientAuthenticatorType\": \"client-secret\",\n" +
        "\"redirectUris\": [\n" +
        "  \"/realms/entando/account/*\"\n" +
        "],\n" +
        "\"webOrigins\": [],\n" +
        "\"notBefore\": 0,\n" +
        "\"bearerOnly\": false,\n" +
        "\"consentRequired\": false,\n" +
        "\"standardFlowEnabled\": true,\n" +
        "\"implicitFlowEnabled\": false,\n" +
        "\"directAccessGrantsEnabled\": false,\n" +
        "\"serviceAccountsEnabled\": false,\n" +
        "\"publicClient\": true,\n" +
        "\"frontchannelLogout\": false,\n" +
        "\"protocol\": \"openid-connect\",\n" +
        "\"attributes\": {},\n" +
        "\"authenticationFlowBindingOverrides\": {},\n" +
        "\"fullScopeAllowed\": false,\n" +
        "\"nodeReRegistrationTimeout\": 0,\n" +
        "\"defaultClientScopes\": [\n" +
        "  \"web-origins\",\n" +
        "  \"roles\",\n" +
        "  \"profile\",\n" +
        "  \"email\"\n" +
        "],\n" +
        "\"optionalClientScopes\": [\n" +
        "  \"address\",\n" +
        "  \"phone\",\n" +
        "  \"offline_access\",\n" +
        "  \"microprofile-jwt\"\n" +
        "],\n" +
        "\"access\": {\n" +
        "  \"view\": true,\n" +
        "  \"configure\": true,\n" +
        "  \"manage\": true\n" +
        "}\n" +
        "  }";

}
