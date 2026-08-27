package com.quoteflow.service;

import com.quoteflow.dto.QuotationDto;
import com.quoteflow.dto.QuotationItemDto;
import com.quoteflow.entity.*;
import com.quoteflow.exception.ResourceNotFoundException;
import com.quoteflow.repository.CustomerRepository;
import com.quoteflow.repository.ProductRepository;
import com.quoteflow.repository.QuotationRepository;
import com.quoteflow.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final QuotationCalculationService calculationService;
    private final QuotationNumberGenerator numberGenerator;
    private final SecurityUtil securityUtil;

    public QuotationService(QuotationRepository quotationRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository,
                            QuotationCalculationService calculationService,
                            QuotationNumberGenerator numberGenerator,
                            SecurityUtil securityUtil) {
        this.quotationRepository = quotationRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.calculationService = calculationService;
        this.numberGenerator = numberGenerator;
        this.securityUtil = securityUtil;
    }

    @Transactional(readOnly = true)
    public List<QuotationDto> getAllQuotations(QuotationStatus status, String query) {
        Long businessId = securityUtil.getCurrentBusinessId();
        List<Quotation> quotations;

        if (query != null && !query.isBlank()) {
            quotations = quotationRepository.searchQuotations(businessId, query.trim());
        } else if (status != null) {
            quotations = quotationRepository.findByBusinessIdAndStatusOrderByCreatedAtDesc(businessId, status);
        } else {
            quotations = quotationRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        }

        return quotations.stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public QuotationDto getQuotationDtoById(Long id) {
        Quotation quotation = getQuotationEntityById(id);
        return mapToDto(quotation);
    }

    @Transactional(readOnly = true)
    public Quotation getQuotationEntityById(Long id) {
        Long businessId = securityUtil.getCurrentBusinessId();
        return quotationRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with ID: " + id));
    }

    @Transactional
    public QuotationDto createQuotation(QuotationDto dto) {
        BusinessProfile bp = securityUtil.getCurrentBusinessProfile();

        Customer customer = customerRepository.findByIdAndBusinessId(dto.getCustomerId(), bp.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + dto.getCustomerId()));

        Quotation quotation = new Quotation();
        quotation.setBusiness(bp);
        quotation.setCustomer(customer);
        quotation.setQuotationNumber(numberGenerator.generateNextNumber(bp));
        quotation.setQuotationDate(dto.getQuotationDate() != null ? dto.getQuotationDate() : LocalDate.now());

        int validityDays = bp.getQuotationValidityDays() != null ? bp.getQuotationValidityDays() : 15;
        quotation.setValidUntil(dto.getValidUntil() != null ? dto.getValidUntil() : quotation.getQuotationDate().plusDays(validityDays));
        quotation.setStatus(dto.getStatus() != null ? dto.getStatus() : QuotationStatus.DRAFT);
        quotation.setDiscountType(dto.getDiscountType() != null ? dto.getDiscountType() : DiscountType.PERCENTAGE);
        quotation.setDiscountValue(dto.getDiscountValue());
        quotation.setNotes(dto.getNotes());
        quotation.setTermsAndConditions(dto.getTermsAndConditions() != null ? dto.getTermsAndConditions() : bp.getTermsAndConditions());

        buildQuotationItems(quotation, dto.getItems(), bp.getId());

        // Perform server-side calculation
        calculationService.calculateQuotation(quotation);

        Quotation saved = quotationRepository.save(quotation);
        return mapToDto(saved);
    }

    @Transactional
    public QuotationDto updateQuotation(Long id, QuotationDto dto) {
        Quotation quotation = getQuotationEntityById(id);
        BusinessProfile bp = securityUtil.getCurrentBusinessProfile();

        if (!quotation.getCustomer().getId().equals(dto.getCustomerId())) {
            Customer newCustomer = customerRepository.findByIdAndBusinessId(dto.getCustomerId(), bp.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + dto.getCustomerId()));
            quotation.setCustomer(newCustomer);
        }

        quotation.setQuotationDate(dto.getQuotationDate());
        quotation.setValidUntil(dto.getValidUntil());
        if (dto.getStatus() != null) {
            quotation.setStatus(dto.getStatus());
        }
        quotation.setDiscountType(dto.getDiscountType());
        quotation.setDiscountValue(dto.getDiscountValue());
        quotation.setNotes(dto.getNotes());
        quotation.setTermsAndConditions(dto.getTermsAndConditions());

        quotation.getItems().clear();
        buildQuotationItems(quotation, dto.getItems(), bp.getId());

        calculationService.calculateQuotation(quotation);

        Quotation updated = quotationRepository.save(quotation);
        return mapToDto(updated);
    }

    @Transactional
    public QuotationDto duplicateQuotation(Long id) {
        Quotation original = getQuotationEntityById(id);
        BusinessProfile bp = securityUtil.getCurrentBusinessProfile();

        Quotation duplicate = new Quotation();
        duplicate.setBusiness(bp);
        duplicate.setCustomer(original.getCustomer());
        duplicate.setQuotationNumber(numberGenerator.generateNextNumber(bp));
        duplicate.setQuotationDate(LocalDate.now());

        int validityDays = bp.getQuotationValidityDays() != null ? bp.getQuotationValidityDays() : 15;
        duplicate.setValidUntil(LocalDate.now().plusDays(validityDays));
        duplicate.setStatus(QuotationStatus.DRAFT);
        duplicate.setDiscountType(original.getDiscountType());
        duplicate.setDiscountValue(original.getDiscountValue());
        duplicate.setNotes(original.getNotes());
        duplicate.setTermsAndConditions(original.getTermsAndConditions());

        for (QuotationItem origItem : original.getItems()) {
            QuotationItem newItem = new QuotationItem();
            newItem.setProduct(origItem.getProduct());
            newItem.setItemName(origItem.getItemName());
            newItem.setDescription(origItem.getDescription());
            newItem.setQuantity(origItem.getQuantity());
            newItem.setUnit(origItem.getUnit());
            newItem.setUnitPrice(origItem.getUnitPrice());
            newItem.setTaxPercentage(origItem.getTaxPercentage());
            newItem.setDiscount(origItem.getDiscount());
            duplicate.addItem(newItem);
        }

        calculationService.calculateQuotation(duplicate);

        Quotation saved = quotationRepository.save(duplicate);
        return mapToDto(saved);
    }

    @Transactional
    public QuotationDto updateStatus(Long id, QuotationStatus newStatus) {
        Quotation quotation = getQuotationEntityById(id);
        quotation.setStatus(newStatus);
        Quotation updated = quotationRepository.save(quotation);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteQuotation(Long id) {
        Quotation quotation = getQuotationEntityById(id);
        quotationRepository.delete(quotation);
    }

    private void buildQuotationItems(Quotation quotation, List<QuotationItemDto> itemDtos, Long businessId) {
        if (itemDtos == null || itemDtos.isEmpty()) {
            throw new IllegalArgumentException("Quotation must contain at least one item");
        }

        for (QuotationItemDto itemDto : itemDtos) {
            QuotationItem item = new QuotationItem();
            if (itemDto.getProductId() != null) {
                Product product = productRepository.findByIdAndBusinessId(itemDto.getProductId(), businessId)
                        .orElse(null);
                item.setProduct(product);
            }
            item.setItemName(itemDto.getItemName().trim());
            item.setDescription(itemDto.getDescription());
            item.setQuantity(itemDto.getQuantity() != null && itemDto.getQuantity() > 0 ? itemDto.getQuantity() : 1);
            item.setUnit(itemDto.getUnit() != null ? itemDto.getUnit() : "Piece");
            item.setUnitPrice(itemDto.getUnitPrice());
            item.setTaxPercentage(itemDto.getTaxPercentage());
            item.setDiscount(itemDto.getDiscount());

            quotation.addItem(item);
        }
    }

    public QuotationDto mapToDto(Quotation q) {
        QuotationDto dto = new QuotationDto();
        dto.setId(q.getId());
        if (q.getCustomer() != null) {
            dto.setCustomerId(q.getCustomer().getId());
            dto.setCustomerName(q.getCustomer().getName());
            dto.setCustomerCompanyName(q.getCustomer().getCompanyName());
            dto.setCustomerEmail(q.getCustomer().getEmail());
            dto.setCustomerPhone(q.getCustomer().getPhone());
        }
        dto.setQuotationNumber(q.getQuotationNumber());
        dto.setQuotationDate(q.getQuotationDate());
        dto.setValidUntil(q.getValidUntil());
        dto.setStatus(q.getStatus());
        dto.setSubtotal(q.getSubtotal());
        dto.setDiscountType(q.getDiscountType());
        dto.setDiscountValue(q.getDiscountValue());
        dto.setDiscountAmount(q.getDiscountAmount());
        dto.setTaxAmount(q.getTaxAmount());
        dto.setGrandTotal(q.getGrandTotal());
        dto.setNotes(q.getNotes());
        dto.setTermsAndConditions(q.getTermsAndConditions());

        if (q.getItems() != null) {
            dto.setItems(q.getItems().stream().map(this::mapItemToDto).toList());
        }
        return dto;
    }

    public QuotationItemDto mapItemToDto(QuotationItem item) {
        QuotationItemDto dto = new QuotationItemDto();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
        }
        dto.setItemName(item.getItemName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnit(item.getUnit());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTaxPercentage(item.getTaxPercentage());
        dto.setDiscount(item.getDiscount());
        dto.setLineSubtotal(item.getLineSubtotal());
        dto.setLineTax(item.getLineTax());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }
}
