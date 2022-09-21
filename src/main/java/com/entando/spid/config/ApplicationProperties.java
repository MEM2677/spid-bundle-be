package com.entando.spid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Template.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

//    @Value("#{systemEnvironment['BILLING_CONTACT_COMPANY'] ?: 'defaultCOMPANY'}")
    private String billingContactCompany;
    private String billingContactEmail;
    private String billingContactPhone;
    private String billingContactRegistryName;
    private String billingContactSiteAddress;
    private String billingContactSiteCity;
    private String billingContactSiteCountry;
    private String billingContactSiteNumber;
    private String billingContactSiteProvince;
    private String billingContactSiteZipCode;
    private String organizationDisplayNames;
    private String organizationNames;
    private String organizationUrls;
    private String otherContactCompany;
    private String otherContactEmail;
    private String otherContactFiscalCode;
    private Boolean otherContactIsSpPrivate;
    private String otherContactIpaCode;
    private String otherContactPhone;
    private String otherContactVatNumber;

    private Boolean spidConfigActive;

    private String keycloakRealm;
    private String keycloakClientId;
    private String keycloakClientSecret;
    private String keycloakAuthUrl;


    public String getBillingContactCompany() {
        return billingContactCompany;
    }

    public void setBillingContactCompany(String billingContactCompany) {
        this.billingContactCompany = billingContactCompany;
    }

    public String getBillingContactEmail() {
        return billingContactEmail;
    }

    public void setBillingContactEmail(String billingContactEmail) {
        this.billingContactEmail = billingContactEmail;
    }

    public String getBillingContactPhone() {
        return billingContactPhone;
    }

    public void setBillingContactPhone(String billingContactPhone) {
        this.billingContactPhone = billingContactPhone;
    }

    public String getBillingContactRegistryName() {
        return billingContactRegistryName;
    }

    public void setBillingContactRegistryName(String billingContactRegistryName) {
        this.billingContactRegistryName = billingContactRegistryName;
    }

    public String getBillingContactSiteAddress() {
        return billingContactSiteAddress;
    }

    public void setBillingContactSiteAddress(String billingContactSiteAddress) {
        this.billingContactSiteAddress = billingContactSiteAddress;
    }

    public String getBillingContactSiteCity() {
        return billingContactSiteCity;
    }

    public void setBillingContactSiteCity(String billingContactSiteCity) {
        this.billingContactSiteCity = billingContactSiteCity;
    }

    public String getBillingContactSiteCountry() {
        return billingContactSiteCountry;
    }

    public void setBillingContactSiteCountry(String billingContactSiteCountry) {
        this.billingContactSiteCountry = billingContactSiteCountry;
    }

    public String getBillingContactSiteNumber() {
        return billingContactSiteNumber;
    }

    public void setBillingContactSiteNumber(String billingContactSiteNumber) {
        this.billingContactSiteNumber = billingContactSiteNumber;
    }

    public String getBillingContactSiteProvince() {
        return billingContactSiteProvince;
    }

    public void setBillingContactSiteProvince(String billingContactSiteProvince) {
        this.billingContactSiteProvince = billingContactSiteProvince;
    }

    public String getBillingContactSiteZipCode() {
        return billingContactSiteZipCode;
    }

    public void setBillingContactSiteZipCode(String billingContactSiteZipCode) {
        this.billingContactSiteZipCode = billingContactSiteZipCode;
    }

    public String getOrganizationDisplayNames() {
        return organizationDisplayNames;
    }

    public void setOrganizationDisplayNames(String organizationDisplayNames) {
        this.organizationDisplayNames = organizationDisplayNames;
    }

    public String getOrganizationNames() {
        return organizationNames;
    }

    public void setOrganizationNames(String organizationNames) {
        this.organizationNames = organizationNames;
    }

    public String getOrganizationUrls() {
        return organizationUrls;
    }

    public void setOrganizationUrls(String organizationUrls) {
        this.organizationUrls = organizationUrls;
    }

    public String getOtherContactCompany() {
        return otherContactCompany;
    }

    public void setOtherContactCompany(String otherContactCompany) {
        this.otherContactCompany = otherContactCompany;
    }

    public String getOtherContactEmail() {
        return otherContactEmail;
    }

    public void setOtherContactEmail(String otherContactEmail) {
        this.otherContactEmail = otherContactEmail;
    }

    public String getOtherContactFiscalCode() {
        return otherContactFiscalCode;
    }

    public void setOtherContactFiscalCode(String otherContactFiscalCode) {
        this.otherContactFiscalCode = otherContactFiscalCode;
    }

    public Boolean getOtherContactIsSpPrivate() {
        return otherContactIsSpPrivate;
    }

    public void setOtherContactIsSpPrivate(Boolean otherContactIsSpPrivate) {
        this.otherContactIsSpPrivate = otherContactIsSpPrivate;
    }

    public String getOtherContactPhone() {
        return otherContactPhone;
    }

    public void setOtherContactPhone(String otherContactPhone) {
        this.otherContactPhone = otherContactPhone;
    }

    public String getOtherContactVatNumber() {
        return otherContactVatNumber;
    }

    public void setOtherContactVatNumber(String otherContactVatNumber) {
        this.otherContactVatNumber = otherContactVatNumber;
    }

    public Boolean getSpidConfigActive() {
        return spidConfigActive;
    }

    public void setSpidConfigActive(Boolean spidConfigActive) {
        this.spidConfigActive = spidConfigActive;
    }

    public String getKeycloakClientSecret() {
        return keycloakClientSecret;
    }

    public void setKeycloakClientSecret(String keycloakClientSecret) {
        this.keycloakClientSecret = keycloakClientSecret;
    }

    public String getKeycloakRealm() {
        return keycloakRealm;
    }

    public void setKeycloakRealm(String keycloakRealm) {
        this.keycloakRealm = keycloakRealm;
    }

    public String getKeycloakClientId() {
        return keycloakClientId;
    }

    public void setKeycloakClientId(String keycloakClientId) {
        this.keycloakClientId = keycloakClientId;
    }

    public String getKeycloakAuthUrl() {
        return keycloakAuthUrl;
    }

    public void setKeycloakAuthUrl(String keycloakAuthUrl) {
        this.keycloakAuthUrl = keycloakAuthUrl;
    }

    public String getOtherContactIpaCode() {
        return otherContactIpaCode;
    }

    public void setOtherContactIpaCode(String otherContactIpaCode) {
        this.otherContactIpaCode = otherContactIpaCode;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
            "billingContactCompany='" + billingContactCompany + '\'' +
            ", billingContactEmail='" + billingContactEmail + '\'' +
            ", billingContactPhone='" + billingContactPhone + '\'' +
            ", billingContactRegistryName='" + billingContactRegistryName + '\'' +
            ", billingContactSiteAddress='" + billingContactSiteAddress + '\'' +
            ", billingContactSiteCity='" + billingContactSiteCity + '\'' +
            ", billingContactSiteCountry='" + billingContactSiteCountry + '\'' +
            ", billingContactSiteNumber='" + billingContactSiteNumber + '\'' +
            ", billingContactSiteProvince='" + billingContactSiteProvince + '\'' +
            ", billingContactSiteZipCode='" + billingContactSiteZipCode + '\'' +
            ", organizationDisplayNames='" + organizationDisplayNames + '\'' +
            ", organizationNames='" + organizationNames + '\'' +
            ", organizationUrls='" + organizationUrls + '\'' +
            ", otherContactCompany='" + otherContactCompany + '\'' +
            ", otherContactEmail='" + otherContactEmail + '\'' +
            ", otherContactFiscalCode='" + otherContactFiscalCode + '\'' +
            ", otherContactIsSpPrivate=" + otherContactIsSpPrivate +
            ", otherContactIpaCode='" + otherContactIpaCode + '\'' +
            ", otherContactPhone='" + otherContactPhone + '\'' +
            ", otherContactVatNumber='" + otherContactVatNumber + '\'' +
            ", spidConfigActive=" + spidConfigActive +
            ", keycloakRealm='" + keycloakRealm + '\'' +
            ", keycloakClientId='" + keycloakClientId + '\'' +
            ", keycloakClientSecret='" + keycloakClientSecret + '\'' +
            ", keycloakAuthUrl='" + keycloakAuthUrl + '\'' +
            '}';
    }
}
