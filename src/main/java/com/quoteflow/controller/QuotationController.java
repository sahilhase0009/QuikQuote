package com.quoteflow.controller;

import com.quoteflow.dto.CustomerDto;
import com.quoteflow.dto.ProductDto;
import com.quoteflow.dto.QuotationDto;
import com.quoteflow.entity.Quotation;
import com.quoteflow.entity.QuotationStatus;
import com.quoteflow.pdf.PdfGeneratorService;
import com.quoteflow.service.BusinessProfileService;
import com.quoteflow.service.CustomerService;
import com.quoteflow.service.ProductService;
import com.quoteflow.service.QuotationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/quotations")
public class QuotationController {

    private final QuotationService quotationService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final BusinessProfileService businessProfileService;
    private final PdfGeneratorService pdfGeneratorService;

    public QuotationController(QuotationService quotationService,
                               CustomerService customerService,
                               ProductService productService,
                               BusinessProfileService businessProfileService,
                               PdfGeneratorService pdfGeneratorService) {
        this.quotationService = quotationService;
        this.customerService = customerService;
        this.productService = productService;
        this.businessProfileService = businessProfileService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping
    public String listQuotations(@RequestParam(value = "status", required = false) QuotationStatus status,
                                 @RequestParam(value = "search", required = false) String search,
                                 Model model) {
        List<QuotationDto> quotations = quotationService.getAllQuotations(status, search);
        model.addAttribute("quotations", quotations);
        model.addAttribute("currentStatus", status);
        model.addAttribute("searchQuery", search);
        model.addAttribute("statuses", QuotationStatus.values());
        return "quotations";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(value = "customerId", required = false) Long customerId, Model model) {
        if (!model.containsAttribute("quotation")) {
            QuotationDto dto = new QuotationDto();
            dto.setQuotationDate(LocalDate.now());
            
            var bp = businessProfileService.getCurrentProfile();
            int validityDays = bp.getQuotationValidityDays() != null ? bp.getQuotationValidityDays() : 15;
            dto.setValidUntil(LocalDate.now().plusDays(validityDays));
            dto.setTermsAndConditions(bp.getTermsAndConditions());
            
            if (customerId != null) {
                dto.setCustomerId(customerId);
            }
            model.addAttribute("quotation", dto);
        }

        populateFormAttributes(model);
        model.addAttribute("isEdit", false);
        return "quotation-form";
    }

    @PostMapping
    public String saveQuotation(@Valid @ModelAttribute("quotation") QuotationDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            populateFormAttributes(model);
            model.addAttribute("isEdit", false);
            return "quotation-form";
        }

        QuotationDto saved = quotationService.createQuotation(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Quotation " + saved.getQuotationNumber() + " created successfully!");
        return "redirect:/quotations/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String viewQuotationDetails(@PathVariable("id") Long id, Model model) {
        QuotationDto dto = quotationService.getQuotationDtoById(id);
        model.addAttribute("quotation", dto);
        model.addAttribute("business", businessProfileService.getCurrentProfile());
        model.addAttribute("statuses", QuotationStatus.values());
        return "quotation-details";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        QuotationDto dto = quotationService.getQuotationDtoById(id);
        model.addAttribute("quotation", dto);
        populateFormAttributes(model);
        model.addAttribute("isEdit", true);
        return "quotation-form";
    }

    @PostMapping("/edit/{id}")
    public String updateQuotation(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("quotation") QuotationDto dto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            populateFormAttributes(model);
            model.addAttribute("isEdit", true);
            return "quotation-form";
        }

        QuotationDto updated = quotationService.updateQuotation(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Quotation updated successfully!");
        return "redirect:/quotations/" + updated.getId();
    }

    @PostMapping("/{id}/duplicate")
    public String duplicateQuotation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        QuotationDto dup = quotationService.duplicateQuotation(id);
        redirectAttributes.addFlashAttribute("successMessage", "Quotation duplicated as draft #" + dup.getQuotationNumber());
        return "redirect:/quotations/" + dup.getId();
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") QuotationStatus status,
                               RedirectAttributes redirectAttributes) {
        quotationService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Quotation status updated to " + status.name());
        return "redirect:/quotations/" + id;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable("id") Long id,
                                              @RequestParam(value = "inline", required = false, defaultValue = "true") boolean inline) {
        Quotation quotation = quotationService.getQuotationEntityForPdf(id);
        byte[] pdfBytes = quotationService.generatePdfForQuotation(id);
        String filename = pdfGeneratorService.generateSanitizedFilename(quotation);

        String disposition = inline ? "inline" : "attachment";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

    @PostMapping("/delete/{id}")
    public String deleteQuotation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        quotationService.deleteQuotation(id);
        redirectAttributes.addFlashAttribute("successMessage", "Quotation deleted successfully!");
        return "redirect:/quotations";
    }

    private void populateFormAttributes(Model model) {
        List<CustomerDto> customers = customerService.getAllCustomers();
        List<ProductDto> products = productService.getActiveProducts();
        model.addAttribute("customers", customers);
        model.addAttribute("products", products);
        model.addAttribute("businessProfile", businessProfileService.getCurrentProfile());
    }
}
