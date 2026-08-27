package com.quoteflow.dto;

import jakarta.validation.constraints.NotBlank;

public class AiEnquiryRequest {

    @NotBlank(message = "Enquiry text is required")
    private String enquiryText;

    private Long customerId;

    public AiEnquiryRequest() {}

    public AiEnquiryRequest(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    public String getEnquiryText() {
        return enquiryText;
    }

    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
