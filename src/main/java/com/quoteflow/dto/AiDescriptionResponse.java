package com.quoteflow.dto;

public class AiDescriptionResponse {

    private String description;

    public AiDescriptionResponse() {}

    public AiDescriptionResponse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
