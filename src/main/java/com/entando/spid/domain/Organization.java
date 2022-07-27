package com.entando.spid.domain;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * PAY ATTENTION: the properties match those of the JSON payload needed by Keycloak when creating a new IdP
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Organization {

    private String organizationNames;
    private String organizationDisplayNames;
    private String organizationUrls;
    private String otherContactCompany;
    private String otherContactPhone;
    private String otherContactEmail;
    private String otherContactFiscalCode;
    private String otherContactIsSpPrivate;
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

    @Override
    public String toString() {
        return "Organization{" +
            "organizationNames='" + organizationNames + '\'' +
            ",\norganizationDisplayNames='" + organizationDisplayNames + '\'' +
            ",\norganizationUrls='" + organizationUrls + '\'' +
            ",\notherContactCompany='" + otherContactCompany + '\'' +
            ",\notherContactPhone='" + otherContactPhone + '\'' +
            ",\notherContactEmail='" + otherContactEmail + '\'' +
            ",\notherContactFiscalCode='" + otherContactFiscalCode + '\'' +
            ",\notherContactIsSpPrivate='" + otherContactIsSpPrivate + '\'' +
            ",\notherContactVatNumber='" + otherContactVatNumber + '\'' +
            ",\nbillingContactCompany='" + billingContactCompany + '\'' +
            ",\nbillingContactPhone='" + billingContactPhone + '\'' +
            ",\nbillingContactEmail='" + billingContactEmail + '\'' +
            ",\nbillingContactRegistryName='" + billingContactRegistryName + '\'' +
            ",\nbillingContactSiteAddress='" + billingContactSiteAddress + '\'' +
            ",\nbillingContactSiteNumber='" + billingContactSiteNumber + '\'' +
            ",\nbillingContactSiteCity='" + billingContactSiteCity + '\'' +
            ",\nbillingContactSiteZipCode='" + billingContactSiteZipCode + '\'' +
            ",\nbillingContactSiteProvince='" + billingContactSiteProvince + '\'' +
            ",\nbillingContactSiteCountry='" + billingContactSiteCountry + '\'' +
            '}';
    }
}
