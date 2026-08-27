package com.quoteflow.controller.api;

import com.quoteflow.ai.AIService;
import com.quoteflow.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiApiController {

    private final AIService aiService;

    public AiApiController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/analyze-enquiry")
    public ResponseEntity<AiEnquiryResponse> analyzeEnquiry(@Valid @RequestBody AiEnquiryRequest request) {
        return ResponseEntity.ok(aiService.analyzeEnquiry(request));
    }

    @PostMapping("/generate-description")
    public ResponseEntity<AiDescriptionResponse> generateDescription(@Valid @RequestBody AiDescriptionRequest request) {
        return ResponseEntity.ok(aiService.generateDescription(request));
    }

    @PostMapping("/generate-followup")
    public ResponseEntity<AiFollowupResponse> generateFollowup(@Valid @RequestBody AiFollowupRequest request) {
        return ResponseEntity.ok(aiService.generateFollowup(request));
    }
}
