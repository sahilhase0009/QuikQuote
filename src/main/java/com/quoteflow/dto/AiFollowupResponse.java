package com.quoteflow.dto;

public class AiFollowupResponse {

    private String message;

    public AiFollowupResponse() {}

    public AiFollowupResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
