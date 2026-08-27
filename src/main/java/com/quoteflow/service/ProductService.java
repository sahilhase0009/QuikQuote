package com.quoteflow.service;

import com.quoteflow.dto.ProductDto;
import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.Product;
import com.quoteflow.exception.ResourceNotFoundException;
import com.quoteflow.repository.ProductRepository;
import com.quoteflow.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;

    public ProductService(ProductRepository productRepository, SecurityUtil securityUtil) {
        this.productRepository = productRepository;
        this.securityUtil = securityUtil;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        Long businessId = securityUtil.getCurrentBusinessId();
        return productRepository.findByBusinessIdOrderByCreatedAtDesc(businessId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getActiveProducts() {
        Long businessId = securityUtil.getCurrentBusinessId();
        return productRepository.findByBusinessIdAndActiveTrueOrderByNameAsc(businessId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String query) {
        Long businessId = securityUtil.getCurrentBusinessId();
        if (query == null || query.isBlank()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(businessId, query.trim())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getProductDtoById(Long id) {
        Product p = getProductEntityById(id);
        return mapToDto(p);
    }

    @Transactional(readOnly = true)
    public Product getProductEntityById(Long id) {
        Long businessId = securityUtil.getCurrentBusinessId();
        return productRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        BusinessProfile bp = securityUtil.getCurrentBusinessProfile();

        Product p = new Product();
        p.setBusiness(bp);
        p.setName(dto.getName().trim());
        p.setDescription(dto.getDescription());
        p.setCategory(dto.getCategory());
        p.setUnit(dto.getUnit() != null ? dto.getUnit() : "Piece");
        p.setPrice(dto.getPrice());
        p.setTaxPercentage(dto.getTaxPercentage() != null ? dto.getTaxPercentage() : bp.getDefaultTaxPercentage());
        p.setActive(dto.isActive());

        Product saved = productRepository.save(p);
        return mapToDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product p = getProductEntityById(id);

        p.setName(dto.getName().trim());
        p.setDescription(dto.getDescription());
        p.setCategory(dto.getCategory());
        p.setUnit(dto.getUnit() != null ? dto.getUnit() : "Piece");
        p.setPrice(dto.getPrice());
        p.setTaxPercentage(dto.getTaxPercentage());
        p.setActive(dto.isActive());

        Product updated = productRepository.save(p);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product p = getProductEntityById(id);
        productRepository.delete(p);
    }

    public ProductDto mapToDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setCategory(p.getCategory());
        dto.setUnit(p.getUnit());
        dto.setPrice(p.getPrice());
        dto.setTaxPercentage(p.getTaxPercentage());
        dto.setActive(p.isActive());
        return dto;
    }
}
