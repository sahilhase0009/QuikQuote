package com.quoteflow.controller;

import com.quoteflow.dto.DashboardStatsDto;
import com.quoteflow.service.DashboardService;
import com.quoteflow.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityUtil securityUtil;

    public DashboardController(DashboardService dashboardService, SecurityUtil securityUtil) {
        this.dashboardService = dashboardService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("business", securityUtil.getCurrentBusinessProfile());
        model.addAttribute("user", securityUtil.getCurrentUser());
        return "dashboard";
    }
}
