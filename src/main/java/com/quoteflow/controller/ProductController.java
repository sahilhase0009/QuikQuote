package com.quoteflow.controller;

import com.quoteflow.dto.ProductDto;
import com.quoteflow.service.BusinessProfileService;
import com.quoteflow.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final BusinessProfileService businessProfileService;

    public ProductController(ProductService productService, BusinessProfileService businessProfileService) {
        this.productService = productService;
        this.businessProfileService = businessProfileService;
    }

    @GetMapping
    public String listProducts(@RequestParam(value = "search", required = false) String search, Model model) {
        List<ProductDto> products = productService.searchProducts(search);
        model.addAttribute("products", products);
        model.addAttribute("searchQuery", search);
        return "products";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("product")) {
            ProductDto dto = new ProductDto();
            dto.setTaxPercentage(businessProfileService.getCurrentProfile().getDefaultTaxPercentage());
            model.addAttribute("product", dto);
        }
        model.addAttribute("isEdit", false);
        return "product-form";
    }

    @PostMapping
    public String saveProduct(@Valid @ModelAttribute("product") ProductDto dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "product-form";
        }

        productService.createProduct(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Product/Service added successfully!");
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        ProductDto dto = productService.getProductDtoById(id);
        model.addAttribute("product", dto);
        model.addAttribute("isEdit", true);
        return "product-form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("product") ProductDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "product-form";
        }

        productService.updateProduct(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Product/Service updated successfully!");
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product/Service deleted successfully!");
        return "redirect:/products";
    }
}
