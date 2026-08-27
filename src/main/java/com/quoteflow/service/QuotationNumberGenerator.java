package com.quoteflow.service;

import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.Quotation;
import com.quoteflow.repository.QuotationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class QuotationNumberGenerator {

    private final QuotationRepository quotationRepository;

    public QuotationNumberGenerator(QuotationRepository quotationRepository) {
        this.quotationRepository = quotationRepository;
    }

    public synchronized String generateNextNumber(BusinessProfile business) {
        String prefix = business.getQuotationPrefix() != null ? business.getQuotationPrefix().trim().toUpperCase() : "QT";
        int currentYear = LocalDate.now().getYear();

        Optional<Quotation> latest = quotationRepository.findTopByBusinessIdOrderByCreatedAtDesc(business.getId());

        int nextSequence = 1;

        if (latest.isPresent() && latest.get().getQuotationNumber() != null) {
            String lastNum = latest.get().getQuotationNumber();
            // Parse sequential counter from format PREFIX-YEAR-NUMBER e.g., QT-2026-0001
            try {
                String[] parts = lastNum.split("-");
                if (parts.length >= 3) {
                    int lastSeq = Integer.parseInt(parts[parts.length - 1]);
                    nextSequence = lastSeq + 1;
                }
            } catch (Exception ignored) {
                // If custom string, fallback to count + 1
                nextSequence = (int) quotationRepository.countByBusinessId(business.getId()) + 1;
            }
        }

        return String.format("%s-%d-%04d", prefix, currentYear, nextSequence);
    }
}
