package com.quoteflow.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quoteflow.dto.*;
import com.quoteflow.entity.Product;
import com.quoteflow.entity.Quotation;
import com.quoteflow.repository.ProductRepository;
import com.quoteflow.repository.QuotationRepository;
import com.quoteflow.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    @Value("${quoteflow.ai.openai-api-key:}")
    private String apiKey;

    @Value("${quoteflow.ai.mock-mode:true}")
    private boolean mockMode;

    @Value("${quoteflow.ai.openai-model:gpt-3.5-turbo}")
    private String model;

    private final ProductRepository productRepository;
    private final QuotationRepository quotationRepository;
    private final SecurityUtil securityUtil;
    private final PromptBuilder promptBuilder;
    private final AIResponseParser responseParser;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public AIService(ProductRepository productRepository,
                     QuotationRepository quotationRepository,
                     SecurityUtil securityUtil,
                     PromptBuilder promptBuilder,
                     AIResponseParser responseParser,
                     ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.quotationRepository = quotationRepository;
        this.securityUtil = securityUtil;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public AiEnquiryResponse analyzeEnquiry(AiEnquiryRequest request) {
        Long businessId = securityUtil.getCurrentBusinessId();
        List<Product> catalogProducts = productRepository.findByBusinessIdAndActiveTrueOrderByNameAsc(businessId);

        AiEnquiryResponse rawResponse;

        if (mockMode || apiKey == null || apiKey.isBlank()) {
            log.info("Running AI analyzeEnquiry in MOCK MODE");
            rawResponse = getMockEnquiryAnalysis(request.getEnquiryText());
        } else {
            try {
                String prompt = promptBuilder.buildEnquiryPrompt(request.getEnquiryText());
                String aiText = callOpenAI(prompt);
                rawResponse = responseParser.parseEnquiryResponse(aiText);
            } catch (Exception e) {
                log.error("OpenAI API call failed, falling back to mock mode", e);
                rawResponse = getMockEnquiryAnalysis(request.getEnquiryText());
            }
        }

        // Backend Catalog Matching (Mandatory AI Safety Rule: Match extracted items to actual business products)
        for (AiEnquiryMatchedItem item : rawResponse.getItems()) {
            matchCatalogProduct(item, catalogProducts);
        }

        return rawResponse;
    }

    public AiDescriptionResponse generateDescription(AiDescriptionRequest request) {
        if (mockMode || apiKey == null || apiKey.isBlank()) {
            log.info("Running AI generateDescription in MOCK MODE");
            return new AiDescriptionResponse(
                "High-performance, durable " + request.getProductName().toLowerCase() + " crafted for modern professional environments, featuring ergonomic design and premium build quality."
            );
        }

        try {
            String prompt = promptBuilder.buildDescriptionPrompt(request.getProductName(), request.getCategory(), request.getKeywords());
            String text = callOpenAI(prompt);
            return new AiDescriptionResponse(text.trim());
        } catch (Exception e) {
            log.error("OpenAI API call failed for description, returning mock fallback", e);
            return new AiDescriptionResponse(
                "Ergonomic " + request.getProductName().toLowerCase() + " designed for comfortable, long-duration business and professional use."
            );
        }
    }

    public AiFollowupResponse generateFollowup(AiFollowupRequest request) {
        Long businessId = securityUtil.getCurrentBusinessId();
        Quotation quotation = quotationRepository.findByIdAndBusinessId(request.getQuotationId(), businessId)
                .orElse(null);

        String customerName = quotation != null && quotation.getCustomer() != null ? quotation.getCustomer().getName() : "Customer";
        String qNumber = quotation != null ? quotation.getQuotationNumber() : "QT-2026-XXXX";
        String grandTotal = quotation != null ? "₹" + quotation.getGrandTotal() : "the discussed amount";
        String validUntil = quotation != null && quotation.getValidUntil() != null ? quotation.getValidUntil().toString() : "soon";

        if (mockMode || apiKey == null || apiKey.isBlank()) {
            log.info("Running AI generateFollowup in MOCK MODE");
            String mockMsg = String.format(
                "Hi %s, following up on quotation %s (Total: %s) sent recently. Please let us know if you have any questions or require any adjustments. We would love to move forward with your order!",
                customerName, qNumber, grandTotal
            );
            return new AiFollowupResponse(mockMsg);
        }

        try {
            String prompt = promptBuilder.buildFollowupPrompt(customerName, qNumber, grandTotal, validUntil, request.getTone());
            String text = callOpenAI(prompt);
            return new AiFollowupResponse(text.trim());
        } catch (Exception e) {
            log.error("OpenAI API call failed for followup, returning mock fallback", e);
            String mockMsg = String.format(
                "Hi %s, just checking in regarding quotation %s. Please let us know if you'd like to proceed or if you need any modifications. Thank you!",
                customerName, qNumber
            );
            return new AiFollowupResponse(mockMsg);
        }
    }

    private void matchCatalogProduct(AiEnquiryMatchedItem item, List<Product> catalog) {
        if (item.getExtractedName() == null) return;
        String query = item.getExtractedName().toLowerCase().trim();

        Product bestMatch = null;
        for (Product p : catalog) {
            String pName = p.getName().toLowerCase().trim();
            if (pName.equals(query) || pName.contains(query) || query.contains(pName)) {
                bestMatch = p;
                break;
            }
        }

        if (bestMatch != null) {
            item.setMatched(true);
            item.setMatchedProductId(bestMatch.getId());
            item.setMatchedProductName(bestMatch.getName());
            item.setPrice(bestMatch.getPrice());
            item.setTaxPercentage(bestMatch.getTaxPercentage());
            item.setUnit(bestMatch.getUnit());
        } else {
            item.setMatched(false);
            item.setMatchedProductName(item.getExtractedName() + " (Not in catalog)");
        }
    }

    private AiEnquiryResponse getMockEnquiryAnalysis(String enquiryText) {
        AiEnquiryResponse resp = new AiEnquiryResponse();
        resp.setSummary("Detected customer request for office seating and furniture items.");
        
        List<String> reqs = new ArrayList<>();
        reqs.add("Ergonomic requirement for seating");
        reqs.add("Delivery requested by next Friday");
        resp.setSpecialRequirements(reqs);

        List<AiEnquiryMatchedItem> items = new ArrayList<>();
        
        String lower = enquiryText != null ? enquiryText.toLowerCase() : "";
        if (lower.contains("table") || lower.contains("desk")) {
            AiEnquiryMatchedItem item1 = new AiEnquiryMatchedItem();
            item1.setExtractedName("Office Table");
            item1.setQuantity(5);
            item1.setExtractedDescription("Modular executive desk with cable management");
            items.add(item1);
        }

        if (lower.contains("chair") || lower.contains("seat") || items.isEmpty()) {
            AiEnquiryMatchedItem item2 = new AiEnquiryMatchedItem();
            item2.setExtractedName("Office Chair");
            item2.setQuantity(20);
            item2.setExtractedDescription("Ergonomic office chair with mesh back");
            items.add(item2);
        }

        resp.setItems(items);
        return resp;
    }

    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);
        
        body.put("messages", messages);
        body.put("temperature", 0.3);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            } catch (Exception e) {
                log.error("Failed to parse OpenAI REST response body", e);
            }
        }
        throw new RuntimeException("OpenAI request failed with status: " + response.getStatusCode());
    }
}
