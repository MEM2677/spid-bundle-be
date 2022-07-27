package com.entando.spid;


import com.entando.spid.service.dto.MapperAttribute;

public interface Constants {

    String DEFAULT_PROTO = "http";

    // constant paths within a POD
//  String CERT_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/ca.crt";
//  String TOKEN_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/token";
    String NAMESPACE_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/namespace";

    // declarations related to the installer itself
    String PROVIDER_FILENAME = "spid-provider.jar";
    String PROVIDER_FILE_LOCAL_PATH = "/spid-provider/" + PROVIDER_FILENAME;
    String PROVIDER_FILE_DESTINATION_PATH = "/opt/jboss/keycloak/standalone/deployments/" + PROVIDER_FILENAME;
    String DEPLOYED_PROVIDER_FILE_DESTINATION_PATH = "/opt/jboss/keycloak/standalone/deployments/" + PROVIDER_FILENAME + ".deployed";

    // kubernetes resource names as created by Entando 7.x
    String KEYCLOACK_POD_NAME_SIGNATURE = "default-sso-in-namespace-deployment";
    //  String DEFAULT_NAMESPACE = "entando";
    String KEYCLOAK_SECRET_NAME = "default-sso-in-namespace-admin-secret";
    String INSTANCE_INGRESS_NAME = "default-sso-in-namespace-ingress";

    // keycloak related values
    String KEYCLOAK_DEFAULT_REALM = "entando";
    String KEYCLOAK_DEFAULT_AUTH_FLOW = "first broker login";
    String KEYCLOAK_NEW_AUTH_FLOW_NAME = "SPID first broker login"; // EDITABLE
    String KEYCLOAK_EXECUTION_HANDLE_EXISTING_ACCOUNT_NAME = KEYCLOAK_NEW_AUTH_FLOW_NAME + " Handle Existing Account";
    String KEYCLOAK_EXECUTION_EXPECTED_DISPLAY_NAME = "Automatically set existing user";
    String KEYCLOAK_EXECUTION_CONFIRM_LINK_DISPLAY_NAME = "Confirm link existing account";
    String KEYCLOAK_EXECUTION_VERIFICATION_OPTIONS_DISPLAY_NAME = KEYCLOAK_NEW_AUTH_FLOW_NAME + " Account verification options";
    String KEYCLOAK_ENTANDO_DISPLAY_NAME = "Entando";

    String ENTANDO_WEB_CLIENT_ID = "entando-web";

    // mapper setup
    MapperAttribute[] KEYCLOAK_IDP_MAPPING = {
        new MapperAttribute("First Name", "name", "firstName"),
        new MapperAttribute("Last Name", "familyName", "lastName"),
        new MapperAttribute("SPID Code", "spidCode", "spid-spidCode"),
        new MapperAttribute("Email", "email", "spid-email"),
        new MapperAttribute("Tax Id", "fiscalNumber", "spid-fiscalNumber"),
        new MapperAttribute("Gender", "gender", "spid-gender"),
        new MapperAttribute("Date of Birth", "dateOfBirth", "spid-dateOfBirth"),
        new MapperAttribute("Place of Birth", "placeOfBirth", "spid-placeOfBirth"),
        new MapperAttribute("County of Birth", "countyOfBirth", "spid-countyOfBirth"),
        new MapperAttribute("Mobile Phone", "mobilePhone", "spid-mobilePhone"),
        new MapperAttribute("Address", "address", "spid-address"),
        new MapperAttribute("Digital Address", "digitalAddress", "spid-digitalAddress"),
        new MapperAttribute("Company Name", "companyName", "spid-companyName"),
        new MapperAttribute("Company Address", "registeredOffice", "spid-registeredOffice"),
        new MapperAttribute("VAT Number", "ivaCode", "spid-ivaCode"),
    };

}
