package com.entando.spid.domain.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;

/*
 {
            "authnContextClassRefs": "[\"https://www.spid.gov.it/SpidL1\"]",
            "otherContactPhone": "+395556935632",
            "postBindingLogout": "true",
            "postBindingResponse": "true",
            "singleLogoutServiceUrl": "https://localhost:8443/demo/samlsso",
            "organizationDisplayNames": "en|Organization, it|Organizzazione",
            "debugEnabled": "true",
            "organizationUrls": "it|http://192-168-49-2.nip.io/entando-de-app, en|http://192-168-49-2.nip.io/auth",
            "xmlSigKeyInfoKeyNameTransformer": "NONE",
            "idpEntityId": "https://localhost:8443/demo",
            "loginHint": "false",
            "allowCreate": "true",
            "organizationNames": "en|Organization, it|Organizzazione",
            "authnContextComparisonType": "minimum",
            "syncMode": "FORCE",
            "singleSignOnServiceUrl": "https://localhost:8443/demo/samlsso",
            "wantAuthnRequestsSigned": "true",
            "validateSignature": "true",
            "signingCertificate": "MIIEGDCCAwCgAwIBAgIJAOrYj9oLEJCwMA0GCSqGSIb3DQEBCwUAMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDAeFw0xOTA0MTExMDAyMDhaFw0yNTAzMDgxMDAyMDhaMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK8kJVo+ugRrbbv9xhXCuVrqi4B7/MQzQc62ocwlFFujJNd4m1mXkUHFbgvwhRkQqo2DAmFeHiwCkJT3K1eeXIFhNFFroEzGPzONyekLpjNvmYIs1CFvirGOj0bkEiGaKEs+/umzGjxIhy5JQlqXE96y1+Izp2QhJimDK0/KNij8I1bzxseP0Ygc4SFveKS+7QO+PrLzWklEWGMs4DM5Zc3VRK7g4LWPWZhKdImC1rnS+/lEmHSvHisdVp/DJtbSrZwSYTRvTTz5IZDSq4kAzrDfpj16h7b3t3nFGc8UoY2Ro4tRZ3ahJ2r3b79yK6C5phY7CAANuW3gDdhVjiBNYs0CAwEAAaOByjCBxzAdBgNVHQ4EFgQU3/7kV2tbdFtphbSA4LH7+w8SkcwwgZcGA1UdIwSBjzCBjIAU3/7kV2tbdFtphbSA4LH7+w8SkcyhaaRnMGUxCzAJBgNVBAYTAklUMQ4wDAYDVQQIEwVJdGFseTENMAsGA1UEBxMEUm9tZTENMAsGA1UEChMEQWdJRDESMBAGA1UECxMJQWdJRCBURVNUMRQwEgYDVQQDEwthZ2lkLmdvdi5pdIIJAOrYj9oLEJCwMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAJNFqXg/V3aimJKUmUaqmQEEoSc3qvXFITvT5f5bKw9yk/NVhR6wndL+z/24h1OdRqs76blgH8k116qWNkkDtt0AlSjQOx5qvFYh1UviOjNdRI4WkYONSw+vuavcx+fB6O5JDHNmMhMySKTnmRqTkyhjrch7zaFIWUSV7hsBuxpqmrWDoLWdXbV3eFH3mINA5AoIY/m0bZtzZ7YNgiFWzxQgekpxd0vcTseMnCcXnsAlctdir0FoCZztxMuZjlBjwLTtM6Ry3/48LMM8Z+lw7NMciKLLTGQyU8XmKKSSOh0dGh5Lrlt5GxIIJkH81C0YimWebz8464QPL3RbLnTKg+c=",
            "nameIDPolicyFormat": "urn:oasis:names:tc:SAML:2.0:nameid-format:transient",
            "principalAttribute": "fiscalNumber",
            "entityId": "http://192.168.1.49:9090/auth/realms/Entando",
            "otherContactCompany": "Entando Srl",
            "signatureAlgorithm": "RSA_SHA256",
            "wantAssertionsEncrypted": "false",
            "useJwksUrl": "true",
            "wantAssertionsSigned": "true",
            "otherContactIpaCode": "bastachesia@gmail.com",
            "postBindingAuthnRequest": "true",
            "forceAuthn": "false",
            "attributeConsumingServiceIndex": "1",
            "addExtensionsElementWithKeyInfo": "false",
            "principalType": "ATTRIBUTE"
        }
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityProviderConfig {

  private String authnContextClassRefs;
  private String otherContactPhone;
  private String postBindingLogout;
  private String postBindingResponse;
  private String singleLogoutServiceUrl;
  private String organizationDisplayNames;
  private String debugEnabled;
  private String organizationUrls;
  private String xmlSigKeyInfoKeyNameTransformer;
  private String idpEntityId;
  private String loginHint;
  private String allowCreate;
  private String organizationNames;
  private String authnContextComparisonType;
  private String syncMode;
  private String singleSignOnServiceUrl;
  private String wantAuthnRequestsSigned;
  private String validateSignature;
  private String signingCertificate;
  private String nameIDPolicyFormat;
  private String principalAttribute;
  private String entityId;
  private String otherContactCompany;
  private String signatureAlgorithm;
  private String wantAssertionsEncrypted;
  private String useJwksUrl;
  private String wantAssertionsSigned;
  private String otherContactIsSpPrivate;
  private String otherContactIpaCode;
  private String postBindingAuthnRequest;
  private String forceAuthn;
  private String attributeConsumingServiceIndex;
  private String addExtensionsElementWithKeyInfo;
  private String principalType;

  public String getAuthnContextClassRefs() {
    return authnContextClassRefs;
  }

  public void setAuthnContextClassRefs(String authnContextClassRefs) {
    this.authnContextClassRefs = authnContextClassRefs;
  }

  public String getOtherContactPhone() {
    return otherContactPhone;
  }

  public void setOtherContactPhone(String otherContactPhone) {
    this.otherContactPhone = otherContactPhone;
  }

  public String getPostBindingLogout() {
    return postBindingLogout;
  }

  public void setPostBindingLogout(String postBindingLogout) {
    this.postBindingLogout = postBindingLogout;
  }

  public String getPostBindingResponse() {
    return postBindingResponse;
  }

  public void setPostBindingResponse(String postBindingResponse) {
    this.postBindingResponse = postBindingResponse;
  }

  public String getSingleLogoutServiceUrl() {
    return singleLogoutServiceUrl;
  }

  public void setSingleLogoutServiceUrl(String singleLogoutServiceUrl) {
    this.singleLogoutServiceUrl = singleLogoutServiceUrl;
  }

  public String getOrganizationDisplayNames() {
    return organizationDisplayNames;
  }

  public void setOrganizationDisplayNames(String organizationDisplayNames) {
    this.organizationDisplayNames = organizationDisplayNames;
  }

  public String getDebugEnabled() {
    return debugEnabled;
  }

  public void setDebugEnabled(String debugEnabled) {
    this.debugEnabled = debugEnabled;
  }

  public String getOrganizationUrls() {
    return organizationUrls;
  }

  public void setOrganizationUrls(String organizationUrls) {
    this.organizationUrls = organizationUrls;
  }

  public String getXmlSigKeyInfoKeyNameTransformer() {
    return xmlSigKeyInfoKeyNameTransformer;
  }

  public void setXmlSigKeyInfoKeyNameTransformer(String xmlSigKeyInfoKeyNameTransformer) {
    this.xmlSigKeyInfoKeyNameTransformer = xmlSigKeyInfoKeyNameTransformer;
  }

  public String getIdpEntityId() {
    return idpEntityId;
  }

  public void setIdpEntityId(String idpEntityId) {
    this.idpEntityId = idpEntityId;
  }

  public String getLoginHint() {
    return loginHint;
  }

  public void setLoginHint(String loginHint) {
    this.loginHint = loginHint;
  }

  public String getAllowCreate() {
    return allowCreate;
  }

  public void setAllowCreate(String allowCreate) {
    this.allowCreate = allowCreate;
  }

  public String getOrganizationNames() {
    return organizationNames;
  }

  public void setOrganizationNames(String organizationNames) {
    this.organizationNames = organizationNames;
  }

  public String getAuthnContextComparisonType() {
    return authnContextComparisonType;
  }

  public void setAuthnContextComparisonType(String authnContextComparisonType) {
    this.authnContextComparisonType = authnContextComparisonType;
  }

  public String getSyncMode() {
    return syncMode;
  }

  public void setSyncMode(String syncMode) {
    this.syncMode = syncMode;
  }

  public String getSingleSignOnServiceUrl() {
    return singleSignOnServiceUrl;
  }

  public void setSingleSignOnServiceUrl(String singleSignOnServiceUrl) {
    this.singleSignOnServiceUrl = singleSignOnServiceUrl;
  }

  public String getWantAuthnRequestsSigned() {
    return wantAuthnRequestsSigned;
  }

  public void setWantAuthnRequestsSigned(String wantAuthnRequestsSigned) {
    this.wantAuthnRequestsSigned = wantAuthnRequestsSigned;
  }

  public String getValidateSignature() {
    return validateSignature;
  }

  public void setValidateSignature(String validateSignature) {
    this.validateSignature = validateSignature;
  }

  public String getSigningCertificate() {
    return signingCertificate;
  }

  public void setSigningCertificate(String signingCertificate) {
    this.signingCertificate = signingCertificate;
  }

  public String getNameIDPolicyFormat() {
    return nameIDPolicyFormat;
  }

  public void setNameIDPolicyFormat(String nameIDPolicyFormat) {
    this.nameIDPolicyFormat = nameIDPolicyFormat;
  }

  public String getPrincipalAttribute() {
    return principalAttribute;
  }

  public void setPrincipalAttribute(String principalAttribute) {
    this.principalAttribute = principalAttribute;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getOtherContactCompany() {
    return otherContactCompany;
  }

  public void setOtherContactCompany(String otherContactCompany) {
    this.otherContactCompany = otherContactCompany;
  }

  public String getSignatureAlgorithm() {
    return signatureAlgorithm;
  }

  public void setSignatureAlgorithm(String signatureAlgorithm) {
    this.signatureAlgorithm = signatureAlgorithm;
  }

  public String getWantAssertionsEncrypted() {
    return wantAssertionsEncrypted;
  }

  public void setWantAssertionsEncrypted(String wantAssertionsEncrypted) {
    this.wantAssertionsEncrypted = wantAssertionsEncrypted;
  }

  public String getUseJwksUrl() {
    return useJwksUrl;
  }

  public void setUseJwksUrl(String useJwksUrl) {
    this.useJwksUrl = useJwksUrl;
  }

  public String getWantAssertionsSigned() {
    return wantAssertionsSigned;
  }

  public void setWantAssertionsSigned(String wantAssertionsSigned) {
    this.wantAssertionsSigned = wantAssertionsSigned;
  }

  public String getOtherContactIpaCode() {
    return otherContactIpaCode;
  }

  public void setOtherContactIpaCode(String otherContactIpaCode) {
    this.otherContactIpaCode = otherContactIpaCode;
  }

  public String getPostBindingAuthnRequest() {
    return postBindingAuthnRequest;
  }

  public void setPostBindingAuthnRequest(String postBindingAuthnRequest) {
    this.postBindingAuthnRequest = postBindingAuthnRequest;
  }

  public String getForceAuthn() {
    return forceAuthn;
  }

  public void setForceAuthn(String forceAuthn) {
    this.forceAuthn = forceAuthn;
  }

  public String getAttributeConsumingServiceIndex() {
    return attributeConsumingServiceIndex;
  }

  public void setAttributeConsumingServiceIndex(String attributeConsumingServiceIndex) {
    this.attributeConsumingServiceIndex = attributeConsumingServiceIndex;
  }

  public String getAddExtensionsElementWithKeyInfo() {
    return addExtensionsElementWithKeyInfo;
  }

  public void setAddExtensionsElementWithKeyInfo(String addExtensionsElementWithKeyInfo) {
    this.addExtensionsElementWithKeyInfo = addExtensionsElementWithKeyInfo;
  }

  public String getPrincipalType() {
    return principalType;
  }

  public void setPrincipalType(String principalType) {
    this.principalType = principalType;
  }

  public String getOtherContactIsSpPrivate() {
    return otherContactIsSpPrivate;
  }

  public void setOtherContactIsSpPrivate(String otherContactIsSpPrivate) {
    this.otherContactIsSpPrivate = otherContactIsSpPrivate;
  }
}
