package com.quoteflow.dto;

import jakarta.validation.constraints.NotBlank;

public class AiDescriptionRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    private String category;
    private String keywords;

    public AiDescriptionRequest() {}

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
