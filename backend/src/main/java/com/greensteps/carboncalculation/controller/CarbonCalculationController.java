package com.greensteps.carboncalculation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greensteps.carboncalculation.dto.CarbonDashboardResponse;
import com.greensteps.carboncalculation.service.CarbonCalculationService;

@RestController
@RequestMapping("/api/carbon")
public class CarbonCalculationController {

  private final CarbonCalculationService carbonCalculationService;

  public CarbonCalculationController(CarbonCalculationService carbonCalculationService) {
    this.carbonCalculationService = carbonCalculationService;
  }

  @GetMapping("/dashboard")
  public CarbonDashboardResponse getDashboard(@RequestParam("userId") Long userId) throws Exception {
    return carbonCalculationService.buildDashboard(userId);
  }
}
