package com.entando.spid.domain;

import com.entando.spid.config.ApplicationProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * PAY ATTENTION: the properties match those of the JSON payload needed by Keycloak when creating a new provider!
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Organization {

    public Organization() {}

    public Organization(ApplicationProperties properties) {
        organizationNames=properties.getOrganizationNames();
        organizationDisplayNames=properties.getOrganizationDisplayNames();
        organizationUrls=properties.getOrganizationUrls();
        otherContactCompany=properties.getOtherContactCompany();
        otherContactPhone=properties.getOtherContactPhone();
        otherContactEmail=properties.getOtherContactEmail();
        otherContactFiscalCode=properties.getOtherContactFiscalCode();
        otherContactIsSpPrivate=properties.getOtherContactIsSpPrivate().toString();
        otherContactIpaCode=properties.getOtherContactIpaCode();
        otherContactVatNumber=properties.getOtherContactVatNumber();
        billingContactCompany=properties.getBillingContactCompany();
        billingContactPhone=properties.getBillingContactPhone();
        billingContactEmail=properties.getBillingContactEmail();
        billingContactRegistryName=properties.getBillingContactRegistryName();
        billingContactSiteAddress=properties.getBillingContactSiteAddress();
        billingContactSiteNumber=properties.getBillingContactSiteNumber();
        billingContactSiteCity=properties.getBillingContactSiteCity();
        billingContactSiteZipCode=properties.getBillingContactSiteZipCode();
        billingContactSiteProvince=properties.getBillingContactSiteProvince();
        billingContactSiteCountry=properties.getBillingContactSiteCountry();
    }

    private String organizationNames;
    private String organizationDisplayNames;
    private String organizationUrls;
    private String otherContactCompany;
    private String otherContactPhone;
    private String otherContactEmail;
    private String otherContactFiscalCode;
    private String otherContactIsSpPrivate;
    private String otherContactIpaCode;
    private String otherContactVatNumber;

    private String billingContactCompany;
    private String billingContactPhone;
    private String billingContactEmail;
    private String billingContactRegistryName;
    private String billingContactSiteAddress;
    private String billingContactSiteNumber;
    private String billingContactSiteCity;
    private String billingContactSiteZipCode;
    private String billingContactSiteProvince;
    private String billingContactSiteCountry;

    public String getOrganizationNames() {
        return organizationNames;
    }

    public void setOrganizationNames(String organizationNames) {
        this.organizationNames = organizationNames;
    }

    public String getOrganizationDisplayNames() {
        return organizationDisplayNames;
    }

    public void setOrganizationDisplayNames(String organizationDisplayNames) {
        this.organizationDisplayNames = organizationDisplayNames;
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

    public String getOtherContactPhone() {
        return otherContactPhone;
    }

    public void setOtherContactPhone(String otherContactPhone) {
        this.otherContactPhone = otherContactPhone;
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

    public String getOtherContactIsSpPrivate() {
        return otherContactIsSpPrivate;
    }

    public void setOtherContactIsSpPrivate(String otherContactIsSpPrivate) {
        this.otherContactIsSpPrivate = otherContactIsSpPrivate;
    }

    public String getBillingContactCompany() {
        return billingContactCompany;
    }

    public void setBillingContactCompany(String billingContactCompany) {
        this.billingContactCompany = billingContactCompany;
    }

    public String getBillingContactPhone() {
        return billingContactPhone;
    }

    public void setBillingContactPhone(String billingContactPhone) {
        this.billingContactPhone = billingContactPhone;
    }

    public String getBillingContactEmail() {
        return billingContactEmail;
    }

    public void setBillingContactEmail(String billingContactEmail) {
        this.billingContactEmail = billingContactEmail;
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

    public String getBillingContactSiteNumber() {
        return billingContactSiteNumber;
    }

    public void setBillingContactSiteNumber(String billingContactSiteNumber) {
        this.billingContactSiteNumber = billingContactSiteNumber;
    }

    public String getBillingContactSiteCity() {
        return billingContactSiteCity;
    }

    public void setBillingContactSiteCity(String billingContactSiteCity) {
        this.billingContactSiteCity = billingContactSiteCity;
    }

    public String getBillingContactSiteZipCode() {
        return billingContactSiteZipCode;
    }

    public void setBillingContactSiteZipCode(String billingContactSiteZipCode) {
        this.billingContactSiteZipCode = billingContactSiteZipCode;
    }

    public String getBillingContactSiteProvince() {
        return billingContactSiteProvince;
    }

    public void setBillingContactSiteProvince(String billingContactSiteProvince) {
        this.billingContactSiteProvince = billingContactSiteProvince;
    }

    public String getBillingContactSiteCountry() {
        return billingContactSiteCountry;
    }

    public void setBillingContactSiteCountry(String billingContactSiteCountry) {
        this.billingContactSiteCountry = billingContactSiteCountry;
    }

    public String getOtherContactVatNumber() {
        return otherContactVatNumber;
    }

    public void setOtherContactVatNumber(String otherContactVatNumber) {
        this.otherContactVatNumber = otherContactVatNumber;
    }

    public String getOtherContactIpaCode() {
        return otherContactIpaCode;
    }

    public void setOtherContactIpaCode(String otherContactIpaCode) {
        this.otherContactIpaCode = otherContactIpaCode;
    }

    @Override
    public String toString() {
        return "Organization{" +
            "organizationNames='" + organizationNames + '\'' +
            ", organizationDisplayNames='" + organizationDisplayNames + '\'' +
            ", organizationUrls='" + organizationUrls + '\'' +
            ", otherContactCompany='" + otherContactCompany + '\'' +
            ", otherContactPhone='" + otherContactPhone + '\'' +
            ", otherContactEmail='" + otherContactEmail + '\'' +
            ", otherContactFiscalCode='" + otherContactFiscalCode + '\'' +
            ", otherContactIsSpPrivate='" + otherContactIsSpPrivate + '\'' +
            ", otherContactIpaCode='" + otherContactIpaCode + '\'' +
            ", otherContactVatNumber='" + otherContactVatNumber + '\'' +
            ", billingContactCompany='" + billingContactCompany + '\'' +
            ", billingContactPhone='" + billingContactPhone + '\'' +
            ", billingContactEmail='" + billingContactEmail + '\'' +
            ", billingContactRegistryName='" + billingContactRegistryName + '\'' +
            ", billingContactSiteAddress='" + billingContactSiteAddress + '\'' +
            ", billingContactSiteNumber='" + billingContactSiteNumber + '\'' +
            ", billingContactSiteCity='" + billingContactSiteCity + '\'' +
            ", billingContactSiteZipCode='" + billingContactSiteZipCode + '\'' +
            ", billingContactSiteProvince='" + billingContactSiteProvince + '\'' +
            ", billingContactSiteCountry='" + billingContactSiteCountry + '\'' +
            '}';
    }
}
