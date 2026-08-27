package com.quoteflow;

import com.quoteflow.dto.QuotationDto;
import com.quoteflow.dto.QuotationItemDto;
import com.quoteflow.entity.DiscountType;
import com.quoteflow.service.QuotationCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuotationCalculationServiceTest {

    private QuotationCalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new QuotationCalculationService();
    }

    @Test
    @DisplayName("Financial Calculation: Single item calculation without discount")
    void testSingleItemCalculation() {
        QuotationItemDto item = new QuotationItemDto();
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("4500.00"));
        item.setTaxPercentage(new BigDecimal("18.00"));
        item.setDiscount(BigDecimal.ZERO);

        calculationService.calculateLineItem(item);

        assertEquals(new BigDecimal("45000.00"), item.getLineSubtotal());
        assertEquals(new BigDecimal("8100.00"), item.getLineTax());
        assertEquals(new BigDecimal("53100.00"), item.getLineTotal());
    }

    @Test
    @DisplayName("Financial Calculation: Quotation with percentage discount and GST")
    void testQuotationWithPercentageDiscount() {
        QuotationDto quotation = new QuotationDto();
        quotation.setDiscountType(DiscountType.PERCENTAGE);
        quotation.setDiscountValue(new BigDecimal("10.00")); // 10% discount

        List<QuotationItemDto> items = new ArrayList<>();
        
        QuotationItemDto item1 = new QuotationItemDto();
        item1.setQuantity(10);
        item1.setUnitPrice(new BigDecimal("4500.00"));
        item1.setTaxPercentage(new BigDecimal("18.00"));
        items.add(item1);

        QuotationItemDto item2 = new QuotationItemDto();
        item2.setQuantity(5);
        item2.setUnitPrice(new BigDecimal("8000.00"));
        item2.setTaxPercentage(new BigDecimal("18.00"));
        items.add(item2);

        quotation.setItems(items);

        calculationService.calculateQuotationDto(quotation);

        // Subtotal = 45000 + 40000 = 85000.00
        assertEquals(new BigDecimal("85000.00"), quotation.getSubtotal());

        // Discount 10% = 8500.00
        assertEquals(new BigDecimal("8500.00"), quotation.getDiscountAmount());

        // Taxable Base = 85000 - 8500 = 76500.00
        // GST 18% on 76500 = 13770.00
        assertEquals(new BigDecimal("13770.00"), quotation.getTaxAmount());

        // Grand Total = 76500 + 13770 = 90270.00
        assertEquals(new BigDecimal("90270.00"), quotation.getGrandTotal());
    }

    @Test
    @DisplayName("Financial Calculation: Quotation with fixed rupee discount")
    void testQuotationWithFixedDiscount() {
        QuotationDto quotation = new QuotationDto();
        quotation.setDiscountType(DiscountType.FIXED);
        quotation.setDiscountValue(new BigDecimal("5000.00")); // ₹5000 discount

        List<QuotationItemDto> items = new ArrayList<>();
        QuotationItemDto item = new QuotationItemDto();
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("4500.00"));
        item.setTaxPercentage(new BigDecimal("18.00"));
        items.add(item);

        quotation.setItems(items);

        calculationService.calculateQuotationDto(quotation);

        assertEquals(new BigDecimal("45000.00"), quotation.getSubtotal());
        assertEquals(new BigDecimal("5000.00"), quotation.getDiscountAmount());
        assertEquals(new BigDecimal("7200.00"), quotation.getTaxAmount());
        assertEquals(new BigDecimal("47200.00"), quotation.getGrandTotal());
    }
}
