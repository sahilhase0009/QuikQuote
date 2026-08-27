package com.quoteflow.controller;

import com.quoteflow.service.CustomerService;
import com.quoteflow.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ai-assistant")
public class AiAssistantController {

    private final CustomerService customerService;
    private final ProductService productService;

    public AiAssistantController(CustomerService customerService, ProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping
    public String aiAssistantPage(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("products", productService.getActiveProducts());
        return "ai-assistant";
    }
}
