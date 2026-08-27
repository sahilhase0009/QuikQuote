package com.quoteflow;

import com.quoteflow.ai.AIService;
import com.quoteflow.dto.AiEnquiryRequest;
import com.quoteflow.dto.AiEnquiryResponse;
import com.quoteflow.repository.ProductRepository;
import com.quoteflow.repository.QuotationRepository;
import com.quoteflow.security.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiServiceTest {

    @Test
    @DisplayName("AI Service: Analyze customer enquiry in Mock Mode")
    void testAiMockEnquiryAnalysis() {
        ProductRepository productRepo = mock(ProductRepository.class);
        QuotationRepository quotationRepo = mock(QuotationRepository.class);
        SecurityUtil securityUtil = mock(SecurityUtil.class);
        
        when(securityUtil.getCurrentBusinessId()).thenReturn(1L);
        when(productRepo.findByBusinessIdAndActiveTrueOrderByNameAsc(anyLong())).thenReturn(Collections.emptyList());

        AIService aiService = new AIService(
            productRepo, quotationRepo, securityUtil, 
            new com.quoteflow.ai.PromptBuilder(), 
            new com.quoteflow.ai.AIResponseParser(new com.fasterxml.jackson.databind.ObjectMapper()),
            new com.fasterxml.jackson.databind.ObjectMapper()
        );

        AiEnquiryRequest request = new AiEnquiryRequest("I need 20 office chairs and 5 office tables");
        AiEnquiryResponse response = aiService.analyzeEnquiry(request);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertFalse(response.getItems().isEmpty());
        assertTrue(response.getItems().stream().anyMatch(i -> i.getExtractedName().contains("Chair")));
    }
}
