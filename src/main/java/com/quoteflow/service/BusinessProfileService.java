package com.quoteflow.service;

import com.quoteflow.dto.BusinessProfileDto;
import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.exception.ResourceNotFoundException;
import com.quoteflow.repository.BusinessProfileRepository;
import com.quoteflow.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;
    private final SecurityUtil securityUtil;

    public BusinessProfileService(BusinessProfileRepository businessProfileRepository, SecurityUtil securityUtil) {
        this.businessProfileRepository = businessProfileRepository;
        this.securityUtil = securityUtil;
    }

    @Transactional(readOnly = true)
    public BusinessProfileDto getCurrentProfile() {
        BusinessProfile bp = securityUtil.getCurrentBusinessProfile();
        return mapToDto(bp);
    }

    @Transactional
    public BusinessProfileDto updateProfile(BusinessProfileDto dto) {
        BusinessProfile bp = securityUtil.getCurrentBusinessProfile();

        bp.setBusinessName(dto.getBusinessName());
        bp.setLogo(dto.getLogo());
        bp.setAddress(dto.getAddress());
        bp.setCity(dto.getCity());
        bp.setState(dto.getState());
        bp.setCountry(dto.getCountry());
        bp.setPincode(dto.getPincode());
        bp.setPhone(dto.getPhone());
        bp.setEmail(dto.getEmail());
        bp.setWebsite(dto.getWebsite());
        bp.setGstNumber(dto.getGstNumber());
        bp.setPanNumber(dto.getPanNumber());
        bp.setDefaultTaxPercentage(dto.getDefaultTaxPercentage());
        bp.setQuotationPrefix(dto.getQuotationPrefix());
        bp.setQuotationValidityDays(dto.getQuotationValidityDays());
        bp.setBankName(dto.getBankName());
        bp.setBankAccountNumber(dto.getBankAccountNumber());
        bp.setIfscCode(dto.getIfscCode());
        bp.setTermsAndConditions(dto.getTermsAndConditions());

        BusinessProfile saved = businessProfileRepository.save(bp);
        return mapToDto(saved);
    }

    public BusinessProfileDto mapToDto(BusinessProfile bp) {
        BusinessProfileDto dto = new BusinessProfileDto();
        dto.setId(bp.getId());
        dto.setBusinessName(bp.getBusinessName());
        dto.setLogo(bp.getLogo());
        dto.setAddress(bp.getAddress());
        dto.setCity(bp.getCity());
        dto.setState(bp.getState());
        dto.setCountry(bp.getCountry());
        dto.setPincode(bp.getPincode());
        dto.setPhone(bp.getPhone());
        dto.setEmail(bp.getEmail());
        dto.setWebsite(bp.getWebsite());
        dto.setGstNumber(bp.getGstNumber());
        dto.setPanNumber(bp.getPanNumber());
        dto.setDefaultTaxPercentage(bp.getDefaultTaxPercentage());
        dto.setQuotationPrefix(bp.getQuotationPrefix());
        dto.setQuotationValidityDays(bp.getQuotationValidityDays());
        dto.setBankName(bp.getBankName());
        dto.setBankAccountNumber(bp.getBankAccountNumber());
        dto.setIfscCode(bp.getIfscCode());
        dto.setTermsAndConditions(bp.getTermsAndConditions());
        return dto;
    }
}
