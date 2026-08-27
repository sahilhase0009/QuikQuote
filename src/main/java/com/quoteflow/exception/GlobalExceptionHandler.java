package com.quoteflow.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        if (isApiRequest(request)) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        model.addAttribute("status", 404);
        model.addAttribute("error", "Resource Not Found");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest request, Model model) {
        log.warn("Access denied: {}", ex.getMessage());
        if (isApiRequest(request)) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 403);
            error.put("error", "Forbidden");
            error.put("message", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }
        model.addAttribute("status", 403);
        model.addAttribute("error", "Access Denied");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleBadRequest(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        log.warn("Bad request: {}", ex.getMessage());
        if (isApiRequest(request)) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        model.addAttribute("status", 400);
        model.addAttribute("error", "Invalid Input");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralError(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled exception occurred: ", ex);
        if (isApiRequest(request)) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("error", "Internal Server Error");
            error.put("message", "An unexpected error occurred. Please try again.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "An unexpected server error occurred. Please try again later.");
        return "error";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        return path.startsWith("/api/") || (acceptHeader != null && acceptHeader.contains("application/json"));
    }
}
