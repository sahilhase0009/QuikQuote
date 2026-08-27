package com.quoteflow.dto;

import java.math.BigDecimal;

public class AiEnquiryMatchedItem {

    private String extractedName;
    private Integer quantity;
    private String extractedDescription;
    private Long matchedProductId;
    private String matchedProductName;
    private BigDecimal price;
    private BigDecimal taxPercentage;
    private String unit;
    private boolean matched;

    public AiEnquiryMatchedItem() {}

    public String getExtractedName() {
        return extractedName;
    }

    public void setExtractedName(String extractedName) {
        this.extractedName = extractedName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getExtractedDescription() {
        return extractedDescription;
    }

    public void setExtractedDescription(String extractedDescription) {
        this.extractedDescription = extractedDescription;
    }

    public Long getMatchedProductId() {
        return matchedProductId;
    }

    public void setMatchedProductId(Long matchedProductId) {
        this.matchedProductId = matchedProductId;
    }

    public String getMatchedProductName() {
        return matchedProductName;
    }

    public void setMatchedProductName(String matchedProductName) {
        this.matchedProductName = matchedProductName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}
