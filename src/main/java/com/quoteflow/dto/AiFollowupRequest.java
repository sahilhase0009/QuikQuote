package com.quoteflow.dto;

import jakarta.validation.constraints.NotNull;

public class AiFollowupRequest {

    @NotNull(message = "Quotation ID is required")
    private Long quotationId;

    private String tone = "Professional";

    public AiFollowupRequest() {}

    public Long getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }
}
