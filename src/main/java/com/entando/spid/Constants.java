package com.entando.spid;


import com.entando.spid.service.dto.MapperAttribute;

public interface Constants {

    String LOCAL_ORGANIZATION_FILE = "spid-organization.json";
    String LOCAL_PROVIDER_FILE = "spid-templates.json";

    String RESOURCE_TEMPLATE_FILE = "idpTemplates.csv";
    String RESOURCE_TEMPLATES_PATH = "config/template/" + RESOURCE_TEMPLATE_FILE;

    // keycloak related values - pay attention
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
