package com.entando.spid.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import com.entando.spid.domain.keycloak.Client;
import com.entando.spid.web.rest.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

class SpidTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Spid.class);
        Spid spid1 = new Spid();
        spid1.setId(1L);
        Spid spid2 = new Spid();
        spid2.setId(spid1.getId());
        assertThat(spid1).isEqualTo(spid2);
        spid2.setId(2L);
        assertThat(spid1).isNotEqualTo(spid2);
        spid1.setId(null);
        assertThat(spid1).isNotEqualTo(spid2);
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


    protected Map<String, String> getEnvMap() {
        Map<String, String> map = new HashMap<>();

        map.put("SPID_AB_SERVICE_PORT_8081_TCP","tcp://10.103.158.194:8081");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP_ADDR","10.102.209.177");
        map.put("SPID_AB_SERVICE_PORT_8081_TCP_PROTO","tcp");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP","tcp://10.102.209.177:8083");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP_PROTO","tcp");
        map.put("SPID_CM_SERVICE_SERVICE_PORT_DE_PORT","8083");
        map.put("SPID_CM_SERVICE_PORT_8083_TCP_PORT","8083");
        map.put("SPID_AB_SERVICE_PORT_8081_TCP_PORT","8081");
        map.put("SPID_AB_SERVICE_SERVICE_PORT","8081");
        map.put("SPID_CM_SERVICE_SERVICE_PORT","8083");
        map.put("SPID_AB_SERVICE_PORT","tcp://10.103.158.194:8081");
        map.put("SPID_AB_SERVICE_PORT_8081_TCP_ADDR","10.103.158.194");
        map.put("SPID_CM_SERVICE_PORT","tcp://10.102.209.177:8083");
        map.put("SPID_AB_SERVICE_SERVICE_PORT_APPBUILDER_PORT","8081");
        map.put("SPID_CM_SERVICE_SERVICE_HOST","10.102.209.177");
        map.put("SPID_AB_SERVICE_SERVICE_HOST","10.103.158.194");
        return map;
    }

    public final static String CLIENT_JSON = "{\n" +
        "    \"id\": \"bcc5b6c3-784a-4e8b-bc7c-da5f6a890bcc\",\n" +
        "    \"clientId\": \"account\",\n" +
        "    \"name\": \"${client_account}\",\n" +
        "    \"rootUrl\": \"${authBaseUrl}\",\n" +
        "    \"baseUrl\": \"/realms/entando/account/\",\n" +
        "    \"surrogateAuthRequired\": false,\n" +
        "    \"enabled\": true,\n" +
        "    \"alwaysDisplayInConsole\": false,\n" +
        "    \"clientAuthenticatorType\": \"client-secret\",\n" +
        "    \"redirectUris\": [\n" +
        "      \"/realms/entando/account/*\"\n" +
        "    ],\n" +
        "    \"webOrigins\": [],\n" +
        "    \"notBefore\": 0,\n" +
        "    \"bearerOnly\": false,\n" +
        "    \"consentRequired\": false,\n" +
        "    \"standardFlowEnabled\": true,\n" +
        "    \"implicitFlowEnabled\": false,\n" +
        "    \"directAccessGrantsEnabled\": false,\n" +
        "    \"serviceAccountsEnabled\": false,\n" +
        "    \"publicClient\": true,\n" +
        "    \"frontchannelLogout\": false,\n" +
        "    \"protocol\": \"openid-connect\",\n" +
        "    \"attributes\": {},\n" +
        "    \"authenticationFlowBindingOverrides\": {},\n" +
        "    \"fullScopeAllowed\": false,\n" +
        "    \"nodeReRegistrationTimeout\": 0,\n" +
        "    \"defaultClientScopes\": [\n" +
        "      \"web-origins\",\n" +
        "      \"roles\",\n" +
        "      \"profile\",\n" +
        "      \"email\"\n" +
        "    ],\n" +
        "    \"optionalClientScopes\": [\n" +
        "      \"address\",\n" +
        "      \"phone\",\n" +
        "      \"offline_access\",\n" +
        "      \"microprofile-jwt\"\n" +
        "    ],\n" +
        "    \"access\": {\n" +
        "      \"view\": true,\n" +
        "      \"configure\": true,\n" +
        "      \"manage\": true\n" +
        "    }\n" +
        "  }";

}
