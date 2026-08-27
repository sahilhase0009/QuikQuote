package com.quoteflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class QuotationItemDto {

    private Long id;
    private Long productId;

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    private String unit = "Piece";

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Tax percentage cannot be negative")
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal lineSubtotal = BigDecimal.ZERO;
    private BigDecimal lineTax = BigDecimal.ZERO;
    private BigDecimal lineTotal = BigDecimal.ZERO;

    public QuotationItemDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getLineSubtotal() {
        return lineSubtotal;
    }

    public void setLineSubtotal(BigDecimal lineSubtotal) {
        this.lineSubtotal = lineSubtotal;
    }

    public BigDecimal getLineTax() {
        return lineTax;
    }

    public void setLineTax(BigDecimal lineTax) {
        this.lineTax = lineTax;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
