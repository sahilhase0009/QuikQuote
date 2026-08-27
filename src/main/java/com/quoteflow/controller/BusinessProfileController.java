package com.quoteflow.controller;

import com.quoteflow.dto.BusinessProfileDto;
import com.quoteflow.service.BusinessProfileService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/business-profile")
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    public BusinessProfileController(BusinessProfileService businessProfileService) {
        this.businessProfileService = businessProfileService;
    }

    @GetMapping
    public String showProfile(Model model) {
        if (!model.containsAttribute("businessProfile")) {
            model.addAttribute("businessProfile", businessProfileService.getCurrentProfile());
        }
        return "business-profile";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("businessProfile") BusinessProfileDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "business-profile";
        }

        businessProfileService.updateProfile(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Business profile updated successfully!");
        return "redirect:/business-profile";
    }
}
