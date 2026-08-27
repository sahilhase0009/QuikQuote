package com.quoteflow.controller;

import com.quoteflow.dto.CustomerDto;
import com.quoteflow.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<CustomerDto> customers = customerService.searchCustomers(search);
        model.addAttribute("customers", customers);
        model.addAttribute("searchQuery", search);
        return "customers";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("customer")) {
            model.addAttribute("customer", new CustomerDto());
        }
        model.addAttribute("isEdit", false);
        return "customer-form";
    }

    @PostMapping
    public String saveCustomer(@Valid @ModelAttribute("customer") CustomerDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "customer-form";
        }

        customerService.createCustomer(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Customer created successfully!");
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        CustomerDto dto = customerService.getCustomerDtoById(id);
        model.addAttribute("customer", dto);
        model.addAttribute("isEdit", true);
        return "customer-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("customer") CustomerDto dto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "customer-form";
        }

        customerService.updateCustomer(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully!");
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        customerService.deleteCustomer(id);
        redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully!");
        return "redirect:/customers";
    }
}
