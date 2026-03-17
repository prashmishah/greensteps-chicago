package com.greensteps.intentrecommendation.controller;

import com.greensteps.intentrecommendation.dto.IntentRecommendationResponse;
import com.greensteps.intentrecommendation.service.IntentRecommendationService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class IntentRecommendationController {

  private final IntentRecommendationService intentRecommendationService;

  public IntentRecommendationController(IntentRecommendationService intentRecommendationService) {
    this.intentRecommendationService = intentRecommendationService;
  }

  @GetMapping
  public IntentRecommendationResponse getRecommendation(
      @RequestParam("mode") String mode,
      @RequestParam("distanceKm") BigDecimal distanceKm
  ) {
    return intentRecommendationService.getRecommendation(mode, distanceKm);
  }
}
