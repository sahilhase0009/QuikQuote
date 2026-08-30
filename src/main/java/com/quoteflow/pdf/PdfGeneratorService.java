package com.quoteflow.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.Customer;
import com.quoteflow.entity.Quotation;
import com.quoteflow.entity.QuotationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorService.class);
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public byte[] generateQuotationPdf(Quotation quotation) {
        if (quotation == null) {
            throw new IllegalArgumentException("Quotation cannot be null");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Color Palette
            Color primaryColor = new Color(30, 41, 59);   // Deep Slate Blue
            Color secondaryColor = new Color(79, 70, 229); // Modern Indigo
            Color lightBgColor = new Color(248, 250, 252); // Light Gray
            Color borderColor = new Color(226, 232, 240);  // Soft Border Gray

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, secondaryColor);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, primaryColor);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, primaryColor);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            Font whiteBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font smallMutedFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

            BusinessProfile business = quotation.getBusiness();
            Customer customer = quotation.getCustomer();

            // --- HEADER TABLE (2 Columns: Business Info vs Quotation Meta) ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60, 40});

            // Business Info Cell
            PdfPCell businessCell = new PdfPCell();
            businessCell.setBorder(Rectangle.NO_BORDER);

            String busNameStr = (business != null && business.getBusinessName() != null && !business.getBusinessName().isBlank())
                    ? business.getBusinessName() : "Business Profile";
            Paragraph busName = new Paragraph(busNameStr, headerFont);
            businessCell.addElement(busName);

            if (business != null) {
                if (business.getAddress() != null) businessCell.addElement(new Paragraph(business.getAddress(), normalFont));
                String cityState = (business.getCity() != null ? business.getCity() : "") + 
                                   (business.getState() != null ? ", " + business.getState() : "") +
                                   (business.getPincode() != null ? " - " + business.getPincode() : "");
                if (!cityState.isBlank()) businessCell.addElement(new Paragraph(cityState, normalFont));
                if (business.getPhone() != null) businessCell.addElement(new Paragraph("Phone: " + business.getPhone(), normalFont));
                if (business.getEmail() != null) businessCell.addElement(new Paragraph("Email: " + business.getEmail(), normalFont));
                if (business.getGstNumber() != null && !business.getGstNumber().isBlank()) {
                    businessCell.addElement(new Paragraph("GSTIN: " + business.getGstNumber(), boldFont));
                }
            }
            headerTable.addCell(businessCell);

            // Quotation Meta Cell
            PdfPCell metaCell = new PdfPCell();
            metaCell.setBorder(Rectangle.NO_BORDER);
            metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            Paragraph qTitle = new Paragraph("QUOTATION", titleFont);
            qTitle.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(qTitle);

            String qNumStr = quotation.getQuotationNumber() != null ? quotation.getQuotationNumber() : "QT-0001";
            Paragraph qNum = new Paragraph("# " + qNumStr, subTitleFont);
            qNum.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(qNum);

            Paragraph qDate = new Paragraph("Date: " + (quotation.getQuotationDate() != null ? quotation.getQuotationDate().format(DATE_FORMATTER) : "-"), normalFont);
            qDate.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(qDate);

            Paragraph qValid = new Paragraph("Valid Until: " + (quotation.getValidUntil() != null ? quotation.getValidUntil().format(DATE_FORMATTER) : "-"), normalFont);
            qValid.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(qValid);

            String statusStr = quotation.getStatus() != null ? quotation.getStatus().name() : "DRAFT";
            Paragraph qStatus = new Paragraph("Status: " + statusStr, boldFont);
            qStatus.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(qStatus);

            headerTable.addCell(metaCell);
            document.add(headerTable);

            document.add(new Paragraph(" ")); // Spacer

            // --- CUSTOMER INFORMATION CARD ---
            PdfPTable custTable = new PdfPTable(1);
            custTable.setWidthPercentage(100);

            PdfPCell custCell = new PdfPCell();
            custCell.setBackgroundColor(lightBgColor);
            custCell.setBorderColor(borderColor);
            custCell.setPadding(10);

            custCell.addElement(new Paragraph("QUOTATION FOR:", boldFont));

            String custNameStr = (customer != null && customer.getName() != null) ? customer.getName() : "Valued Customer";
            String compNameStr = (customer != null && customer.getCompanyName() != null && !customer.getCompanyName().isBlank())
                    ? " (" + customer.getCompanyName() + ")" : "";
            
            custCell.addElement(new Paragraph(custNameStr + compNameStr, subTitleFont));

            if (customer != null) {
                if (customer.getAddress() != null) custCell.addElement(new Paragraph(customer.getAddress(), normalFont));
                String custCityState = (customer.getCity() != null ? customer.getCity() : "") + 
                                       (customer.getState() != null ? ", " + customer.getState() : "") +
                                       (customer.getPincode() != null ? " - " + customer.getPincode() : "");
                if (!custCityState.isBlank()) custCell.addElement(new Paragraph(custCityState, normalFont));
                if (customer.getPhone() != null) custCell.addElement(new Paragraph("Phone: " + customer.getPhone(), normalFont));
                if (customer.getEmail() != null) custCell.addElement(new Paragraph("Email: " + customer.getEmail(), normalFont));
            }

            custTable.addCell(custCell);
            document.add(custTable);

            document.add(new Paragraph(" ")); // Spacer

            // --- LINE ITEMS TABLE ---
            PdfPTable itemsTable = new PdfPTable(6);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{35, 10, 10, 15, 12, 18});

            // Table Header
            String[] headers = {"Item & Description", "Qty", "Unit", "Unit Price", "Tax %", "Line Total"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, whiteBoldFont));
                cell.setBackgroundColor(secondaryColor);
                cell.setPadding(8);
                cell.setHorizontalAlignment(h.contains("Total") || h.contains("Price") ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
                itemsTable.addCell(cell);
            }

            // Table Rows
            if (quotation.getItems() != null) {
                for (QuotationItem item : quotation.getItems()) {
                    // Item Name & Description
                    PdfPCell descCell = new PdfPCell();
                    descCell.setPadding(6);
                    String itemName = item.getItemName() != null ? item.getItemName() : "Service/Product";
                    descCell.addElement(new Paragraph(itemName, boldFont));
                    if (item.getDescription() != null && !item.getDescription().isBlank()) {
                        descCell.addElement(new Paragraph(item.getDescription(), smallMutedFont));
                    }
                    itemsTable.addCell(descCell);

                    // Qty
                    PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                    qtyCell.setPadding(6);
                    qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemsTable.addCell(qtyCell);

                    // Unit
                    PdfPCell unitCell = new PdfPCell(new Phrase(item.getUnit() != null ? item.getUnit() : "Piece", normalFont));
                    unitCell.setPadding(6);
                    unitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemsTable.addCell(unitCell);

                    // Unit Price
                    PdfPCell priceCell = new PdfPCell(new Phrase(formatAmount(item.getUnitPrice()), normalFont));
                    priceCell.setPadding(6);
                    priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    itemsTable.addCell(priceCell);

                    // Tax %
                    PdfPCell taxCell = new PdfPCell(new Phrase((item.getTaxPercentage() != null ? item.getTaxPercentage() : BigDecimal.ZERO) + "%", normalFont));
                    taxCell.setPadding(6);
                    taxCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemsTable.addCell(taxCell);

                    // Line Total
                    PdfPCell totalCell = new PdfPCell(new Phrase(formatAmount(item.getLineTotal()), boldFont));
                    totalCell.setPadding(6);
                    totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    itemsTable.addCell(totalCell);
                }
            }

            document.add(itemsTable);

            document.add(new Paragraph(" ")); // Spacer

            // --- SUMMARY / TOTALS BREAKDOWN TABLE ---
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(45);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.setWidths(new float[]{50, 50});

            addSummaryRow(summaryTable, "Subtotal:", formatAmount(quotation.getSubtotal()), normalFont);

            if (quotation.getDiscountAmount() != null && quotation.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                addSummaryRow(summaryTable, "Discount:", "- " + formatAmount(quotation.getDiscountAmount()), normalFont);
            }

            addSummaryRow(summaryTable, "Tax Amount:", formatAmount(quotation.getTaxAmount()), normalFont);
            addSummaryRow(summaryTable, "Grand Total:", formatAmount(quotation.getGrandTotal()), titleFont);

            document.add(summaryTable);

            document.add(new Paragraph(" ")); // Spacer

            // --- BANK DETAILS & TERMS AND CONDITIONS ---
            PdfPTable bottomTable = new PdfPTable(2);
            bottomTable.setWidthPercentage(100);
            bottomTable.setWidths(new float[]{50, 50});

            // Bank Details Cell
            PdfPCell bankCell = new PdfPCell();
            bankCell.setBorder(Rectangle.NO_BORDER);
            if (business != null && business.getBankName() != null && !business.getBankName().isBlank()) {
                bankCell.addElement(new Paragraph("BANK & PAYMENT DETAILS", boldFont));
                bankCell.addElement(new Paragraph("Bank Name: " + business.getBankName(), normalFont));
                if (business.getBankAccountNumber() != null) bankCell.addElement(new Paragraph("A/C No: " + business.getBankAccountNumber(), normalFont));
                if (business.getIfscCode() != null) bankCell.addElement(new Paragraph("IFSC Code: " + business.getIfscCode(), normalFont));
            }
            bottomTable.addCell(bankCell);

            // Authorized Signature Cell
            PdfPCell sigCell = new PdfPCell();
            sigCell.setBorder(Rectangle.NO_BORDER);
            sigCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sigCell.addElement(new Paragraph("For " + busNameStr, boldFont));
            sigCell.addElement(new Paragraph("\n\n_______________________\nAuthorized Signatory", normalFont));
            bottomTable.addCell(sigCell);

            document.add(bottomTable);

            // Terms & Conditions
            if (quotation.getTermsAndConditions() != null && !quotation.getTermsAndConditions().isBlank()) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("TERMS & CONDITIONS", boldFont));
                document.add(new Paragraph(quotation.getTermsAndConditions(), normalFont));
            }

            // Footer
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Thank you for your business! | Generated by QuikQuote", smallMutedFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            log.error("Failed to generate PDF for quotation ID: {}", quotation.getId(), e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }

        return out.toByteArray();
    }

    public String generateSanitizedFilename(Quotation quotation) {
        String number = quotation != null && quotation.getQuotationNumber() != null ? quotation.getQuotationNumber() : "QT-000";
        Customer customer = quotation != null ? quotation.getCustomer() : null;
        String customerName = customer != null && customer.getName() != null ? customer.getName() : "Customer";
        
        String safeCustomer = customerName.replaceAll("[^a-zA-Z0-9.-]", "-").replaceAll("-+", "-");
        return String.format("%s-%s.pdf", number, safeCustomer);
    }

    private String formatAmount(BigDecimal val) {
        if (val == null) {
            return "Rs. 0.00";
        }
        return "Rs. " + CURRENCY_FORMAT.format(val);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, font));
        lCell.setBorder(Rectangle.NO_BORDER);
        lCell.setPadding(4);

        PdfPCell vCell = new PdfPCell(new Phrase(value, font));
        vCell.setBorder(Rectangle.NO_BORDER);
        vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        vCell.setPadding(4);

        table.addCell(lCell);
        table.addCell(vCell);
    }
}
