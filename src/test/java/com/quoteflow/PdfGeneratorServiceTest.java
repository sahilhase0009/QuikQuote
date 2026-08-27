package com.quoteflow;

import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.Customer;
import com.quoteflow.entity.Quotation;
import com.quoteflow.entity.QuotationItem;
import com.quoteflow.entity.QuotationStatus;
import com.quoteflow.pdf.PdfGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PdfGeneratorServiceTest {

    private PdfGeneratorService pdfGeneratorService;

    @BeforeEach
    void setUp() {
        pdfGeneratorService = new PdfGeneratorService();
    }

    @Test
    @DisplayName("PDF Generation: Generate non-empty PDF byte array for valid quotation")
    void testPdfGeneration() {
        BusinessProfile bp = new BusinessProfile();
        bp.setBusinessName("Prime Office Solutions");
        bp.setAddress("102 MG Road");
        bp.setPhone("+91 9876543210");
        bp.setGstNumber("27AAAAA0000A1Z5");
        bp.setBankName("HDFC Bank");
        bp.setBankAccountNumber("50200012345678");

        Customer cust = new Customer();
        cust.setName("Rahul Patil");
        cust.setCompanyName("Apex Tech");

        Quotation q = new Quotation();
        q.setBusiness(bp);
        q.setCustomer(cust);
        q.setQuotationNumber("QT-2026-0001");
        q.setQuotationDate(LocalDate.now());
        q.setStatus(QuotationStatus.SENT);
        q.setSubtotal(new BigDecimal("45000.00"));
        q.setTaxAmount(new BigDecimal("8100.00"));
        q.setGrandTotal(new BigDecimal("53100.00"));

        QuotationItem item = new QuotationItem();
        item.setItemName("Office Chair");
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("4500.00"));
        item.setTaxPercentage(new BigDecimal("18.00"));
        item.setLineSubtotal(new BigDecimal("45000.00"));
        item.setLineTax(new BigDecimal("8100.00"));
        item.setLineTotal(new BigDecimal("53100.00"));
        q.addItem(item);

        byte[] pdfBytes = pdfGeneratorService.generateQuotationPdf(q);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "Generated PDF should be non-empty and greater than 500 bytes");

        // Test filename sanitization
        String filename = pdfGeneratorService.generateSanitizedFilename(q);
        assertEquals("QT-2026-0001-Rahul-Patil.pdf", filename);
    }
}
