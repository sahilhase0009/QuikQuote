package com.quoteflow.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quoteflow.dto.AiEnquiryMatchedItem;
import com.quoteflow.dto.AiEnquiryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AIResponseParser {

    private static final Logger log = LoggerFactory.getLogger(AIResponseParser.class);
    private final ObjectMapper objectMapper;

    public AIResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiEnquiryResponse parseEnquiryResponse(String rawContent) {
        AiEnquiryResponse response = new AiEnquiryResponse();
        try {
            String jsonStr = extractJsonSubstring(rawContent);
            JsonNode root = objectMapper.readTree(jsonStr);

            if (root.has("summary")) {
                response.setSummary(root.get("summary").asText());
            }

            if (root.has("specialRequirements") && root.get("specialRequirements").isArray()) {
                List<String> reqs = new ArrayList<>();
                for (JsonNode req : root.get("specialRequirements")) {
                    reqs.add(req.asText());
                }
                response.setSpecialRequirements(reqs);
            }

            if (root.has("items") && root.get("items").isArray()) {
                List<AiEnquiryMatchedItem> items = new ArrayList<>();
                for (JsonNode itemNode : root.get("items")) {
                    AiEnquiryMatchedItem item = new AiEnquiryMatchedItem();
                    item.setExtractedName(itemNode.has("name") ? itemNode.get("name").asText() : "Item");
                    item.setQuantity(itemNode.has("quantity") ? itemNode.get("quantity").asInt(1) : 1);
                    item.setExtractedDescription(itemNode.has("description") ? itemNode.get("description").asText() : "");
                    items.add(item);
                }
                response.setItems(items);
            }
        } catch (Exception e) {
            log.error("Failed to parse AI JSON response: {}", rawContent, e);
            response.setSummary("Extracted customer requirements");
        }
        return response;
    }

    private String extractJsonSubstring(String text) {
        if (text == null) return "{}";
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return text.substring(firstBrace, lastBrace + 1);
        }
        return text;
    }
}
