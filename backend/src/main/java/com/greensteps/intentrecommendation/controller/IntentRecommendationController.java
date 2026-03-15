package com.greensteps.intentrecommendation.controller;

import com.greensteps.intentrecommendation.dto.IntentRecommendationResponse;
import com.greensteps.intentrecommendation.service.IntentRecommendationService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class IntentRecommendationController {

  private static final BigDecimal KM_TO_MILES = new BigDecimal("0.621371");

  private final IntentRecommendationService intentRecommendationService;

  public IntentRecommendationController(IntentRecommendationService intentRecommendationService) {
    this.intentRecommendationService = intentRecommendationService;
  }

  @GetMapping
  public IntentRecommendationResponse getRecommendation(
      @RequestParam("mode") String mode,
      @RequestParam("distanceKm") BigDecimal distanceKm
  ) {
    BigDecimal miles = distanceKm == null ? null : distanceKm.multiply(KM_TO_MILES)
        .setScale(3, RoundingMode.HALF_UP);
    return intentRecommendationService.getRecommendation(mode, miles);
  }
}
