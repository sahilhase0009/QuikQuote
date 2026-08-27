package com.quoteflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BusinessProfileDto {

    private Long id;

    @NotBlank(message = "Business name is required")
    private String businessName;

    private String logo;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String phone;
    private String email;
    private String website;
    private String gstNumber;
    private String panNumber;

    @NotNull(message = "Default tax percentage is required")
    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal defaultTaxPercentage;

    @NotBlank(message = "Quotation prefix is required")
    private String quotationPrefix;

    @NotNull(message = "Quotation validity days is required")
    @Min(value = 1, message = "Validity must be at least 1 day")
    private Integer quotationValidityDays;

    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;
    private String termsAndConditions;

    public BusinessProfileDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public BigDecimal getDefaultTaxPercentage() {
        return defaultTaxPercentage;
    }

    public void setDefaultTaxPercentage(BigDecimal defaultTaxPercentage) {
        this.defaultTaxPercentage = defaultTaxPercentage;
    }

    public String getQuotationPrefix() {
        return quotationPrefix;
    }

    public void setQuotationPrefix(String quotationPrefix) {
        this.quotationPrefix = quotationPrefix;
    }

    public Integer getQuotationValidityDays() {
        return quotationValidityDays;
    }

    public void setQuotationValidityDays(Integer quotationValidityDays) {
        this.quotationValidityDays = quotationValidityDays;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
