package com.quoteflow.dto;

import java.util.ArrayList;
import java.util.List;

public class AiEnquiryResponse {

    private List<AiEnquiryMatchedItem> items = new ArrayList<>();
    private List<String> specialRequirements = new ArrayList<>();
    private String summary;

    public AiEnquiryResponse() {}

    public List<AiEnquiryMatchedItem> getItems() {
        return items;
    }

    public void setItems(List<AiEnquiryMatchedItem> items) {
        this.items = items;
    }

    public List<String> getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(List<String> specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
