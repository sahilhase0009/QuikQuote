package com.quoteflow.ai;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildEnquiryPrompt(String enquiryText) {
        return """
            You are an AI assistant for a business quotation management software.
            Analyze the following customer enquiry and extract requested items, quantities, and special requirements.
            
            Customer Enquiry:
            \"""%s\"""
            
            Return ONLY a valid JSON object with the following exact structure:
            {
              "items": [
                {
                  "name": "Item/Product Name",
                  "quantity": 1,
                  "description": "Brief summary of item requirement"
                }
              ],
              "specialRequirements": [
                "Requirement 1",
                "Requirement 2"
              ],
              "summary": "Brief overall summary of enquiry"
            }
            Do NOT invent prices, taxes, or discounts. Keep product names standard.
            """.formatted(enquiryText);
    }

    public String buildDescriptionPrompt(String productName, String category, String keywords) {
        return """
            You are an expert copywriter for business product and service catalogs.
            Generate a concise, high-converting, professional description (25-45 words) for the following product or service:
            
            Product Name: %s
            Category: %s
            Keywords/Context: %s
            
            Return ONLY the generated description string without quotes or extra text.
            """.formatted(productName, category != null ? category : "General", keywords != null ? keywords : "Quality");
    }

    public String buildFollowupPrompt(String customerName, String quotationNumber, String amount, String validUntil, String tone) {
        return """
            Write a polite, persuasive, and professional follow-up message (under 60 words) from a business owner to a customer regarding an sent quotation.
            
            Customer Name: %s
            Quotation Number: %s
            Quotation Amount: %s
            Valid Until: %s
            Message Tone: %s
            
            Return ONLY the final message text ready to copy-paste.
            """.formatted(customerName, quotationNumber, amount, validUntil != null ? validUntil : "Soon", tone);
    }
}
