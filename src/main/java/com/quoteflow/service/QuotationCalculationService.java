package com.quoteflow.service;

import com.quoteflow.dto.QuotationDto;
import com.quoteflow.dto.QuotationItemDto;
import com.quoteflow.entity.DiscountType;
import com.quoteflow.entity.Quotation;
import com.quoteflow.entity.QuotationItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class QuotationCalculationService {

    public void calculateLineItem(QuotationItem item) {
        BigDecimal qty = BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1);
        BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal taxPct = item.getTaxPercentage() != null ? item.getTaxPercentage() : BigDecimal.ZERO;
        BigDecimal itemDiscount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;

        BigDecimal rawSubtotal = qty.multiply(price).subtract(itemDiscount);
        if (rawSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            rawSubtotal = BigDecimal.ZERO;
        }

        BigDecimal subtotal = rawSubtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(taxPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        item.setLineSubtotal(subtotal);
        item.setLineTax(tax);
        item.setLineTotal(total);
    }

    public void calculateLineItem(QuotationItemDto item) {
        BigDecimal qty = BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1);
        BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal taxPct = item.getTaxPercentage() != null ? item.getTaxPercentage() : BigDecimal.ZERO;
        BigDecimal itemDiscount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;

        BigDecimal rawSubtotal = qty.multiply(price).subtract(itemDiscount);
        if (rawSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            rawSubtotal = BigDecimal.ZERO;
        }

        BigDecimal subtotal = rawSubtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(taxPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        item.setLineSubtotal(subtotal);
        item.setLineTax(tax);
        item.setLineTotal(total);
    }

    public void calculateQuotation(Quotation quotation) {
        BigDecimal quotationSubtotal = BigDecimal.ZERO;
        BigDecimal quotationTax = BigDecimal.ZERO;

        if (quotation.getItems() != null) {
            for (QuotationItem item : quotation.getItems()) {
                calculateLineItem(item);
                quotationSubtotal = quotationSubtotal.add(item.getLineSubtotal());
                quotationTax = quotationTax.add(item.getLineTax());
            }
        }

        quotationSubtotal = quotationSubtotal.setScale(2, RoundingMode.HALF_UP);
        quotation.setSubtotal(quotationSubtotal);

        // Calculate Discount
        BigDecimal discountVal = quotation.getDiscountValue() != null ? quotation.getDiscountValue() : BigDecimal.ZERO;
        BigDecimal discountAmt = BigDecimal.ZERO;

        if (quotation.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmt = quotationSubtotal.multiply(discountVal).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else if (quotation.getDiscountType() == DiscountType.FIXED) {
            discountAmt = discountVal.setScale(2, RoundingMode.HALF_UP);
        }

        if (discountAmt.compareTo(quotationSubtotal) > 0) {
            discountAmt = quotationSubtotal;
        }
        quotation.setDiscountAmount(discountAmt);

        // Taxable Base & Recalculate Tax if discount applied at header
        BigDecimal taxableBase = quotationSubtotal.subtract(discountAmt);
        if (taxableBase.compareTo(BigDecimal.ZERO) < 0) {
            taxableBase = BigDecimal.ZERO;
        }

        // Adjust tax proportionally if global discount is applied
        if (quotationSubtotal.compareTo(BigDecimal.ZERO) > 0 && discountAmt.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountRatio = taxableBase.divide(quotationSubtotal, 10, RoundingMode.HALF_UP);
            quotationTax = quotationTax.multiply(discountRatio).setScale(2, RoundingMode.HALF_UP);
        }

        quotation.setTaxAmount(quotationTax);

        BigDecimal grandTotal = taxableBase.add(quotationTax).setScale(2, RoundingMode.HALF_UP);
        quotation.setGrandTotal(grandTotal);
    }

    public void calculateQuotationDto(QuotationDto dto) {
        BigDecimal quotationSubtotal = BigDecimal.ZERO;
        BigDecimal quotationTax = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (QuotationItemDto item : dto.getItems()) {
                calculateLineItem(item);
                quotationSubtotal = quotationSubtotal.add(item.getLineSubtotal());
                quotationTax = quotationTax.add(item.getLineTax());
            }
        }

        quotationSubtotal = quotationSubtotal.setScale(2, RoundingMode.HALF_UP);
        dto.setSubtotal(quotationSubtotal);

        BigDecimal discountVal = dto.getDiscountValue() != null ? dto.getDiscountValue() : BigDecimal.ZERO;
        BigDecimal discountAmt = BigDecimal.ZERO;

        if (dto.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmt = quotationSubtotal.multiply(discountVal).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else if (dto.getDiscountType() == DiscountType.FIXED) {
            discountAmt = discountVal.setScale(2, RoundingMode.HALF_UP);
        }

        if (discountAmt.compareTo(quotationSubtotal) > 0) {
            discountAmt = quotationSubtotal;
        }
        dto.setDiscountAmount(discountAmt);

        BigDecimal taxableBase = quotationSubtotal.subtract(discountAmt);
        if (taxableBase.compareTo(BigDecimal.ZERO) < 0) {
            taxableBase = BigDecimal.ZERO;
        }

        if (quotationSubtotal.compareTo(BigDecimal.ZERO) > 0 && discountAmt.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountRatio = taxableBase.divide(quotationSubtotal, 10, RoundingMode.HALF_UP);
            quotationTax = quotationTax.multiply(discountRatio).setScale(2, RoundingMode.HALF_UP);
        }

        dto.setTaxAmount(quotationTax);

        BigDecimal grandTotal = taxableBase.add(quotationTax).setScale(2, RoundingMode.HALF_UP);
        dto.setGrandTotal(grandTotal);
    }
}
