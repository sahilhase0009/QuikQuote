package com.quoteflow.service;

import com.quoteflow.dto.DashboardStatsDto;
import com.quoteflow.entity.QuotationStatus;
import com.quoteflow.repository.QuotationRepository;
import com.quoteflow.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final QuotationRepository quotationRepository;
    private final QuotationService quotationService;
    private final SecurityUtil securityUtil;

    public DashboardService(QuotationRepository quotationRepository, QuotationService quotationService, SecurityUtil securityUtil) {
        this.quotationRepository = quotationRepository;
        this.quotationService = quotationService;
        this.securityUtil = securityUtil;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        Long businessId = securityUtil.getCurrentBusinessId();

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalQuotations(quotationRepository.countByBusinessId(businessId));
        stats.setDraftCount(quotationRepository.countByBusinessIdAndStatus(businessId, QuotationStatus.DRAFT));
        stats.setSentCount(quotationRepository.countByBusinessIdAndStatus(businessId, QuotationStatus.SENT));
        stats.setAcceptedCount(quotationRepository.countByBusinessIdAndStatus(businessId, QuotationStatus.ACCEPTED));
        stats.setRejectedCount(quotationRepository.countByBusinessIdAndStatus(businessId, QuotationStatus.REJECTED));

        stats.setTotalValue(quotationRepository.sumGrandTotalByBusinessId(businessId));
        stats.setAcceptedValue(quotationRepository.sumGrandTotalByBusinessIdAndStatus(businessId, QuotationStatus.ACCEPTED));
        stats.setPendingValue(quotationRepository.sumPendingGrandTotalByBusinessId(businessId));

        stats.setRecentQuotations(
            quotationRepository.findTop5ByBusinessIdOrderByCreatedAtDesc(businessId)
                .stream()
                .map(quotationService::mapToDto)
                .toList()
        );

        return stats;
    }
}
