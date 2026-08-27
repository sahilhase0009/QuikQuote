package com.quoteflow.controller.api;

import com.quoteflow.dto.QuotationDto;
import com.quoteflow.entity.Quotation;
import com.quoteflow.entity.QuotationStatus;
import com.quoteflow.pdf.PdfGeneratorService;
import com.quoteflow.service.QuotationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
public class QuotationApiController {

    private final QuotationService quotationService;
    private final PdfGeneratorService pdfGeneratorService;

    public QuotationApiController(QuotationService quotationService, PdfGeneratorService pdfGeneratorService) {
        this.quotationService = quotationService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping
    public ResponseEntity<List<QuotationDto>> getAllQuotations(@RequestParam(value = "status", required = false) QuotationStatus status,
                                                               @RequestParam(value = "query", required = false) String query) {
        return ResponseEntity.ok(quotationService.getAllQuotations(status, query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationDto> getQuotationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(quotationService.getQuotationDtoById(id));
    }

    @PostMapping
    public ResponseEntity<QuotationDto> createQuotation(@Valid @RequestBody QuotationDto dto) {
        QuotationDto created = quotationService.createQuotation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuotationDto> updateQuotation(@PathVariable("id") Long id, @Valid @RequestBody QuotationDto dto) {
        return ResponseEntity.ok(quotationService.updateQuotation(id, dto));
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<QuotationDto> duplicateQuotation(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quotationService.duplicateQuotation(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<QuotationDto> updateStatus(@PathVariable("id") Long id, @RequestParam("status") QuotationStatus status) {
        return ResponseEntity.ok(quotationService.updateStatus(id, status));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getQuotationPdf(@PathVariable("id") Long id) {
        Quotation quotation = quotationService.getQuotationEntityById(id);
        byte[] pdf = pdfGeneratorService.generateQuotationPdf(quotation);
        String filename = pdfGeneratorService.generateSanitizedFilename(quotation);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable("id") Long id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }
}
