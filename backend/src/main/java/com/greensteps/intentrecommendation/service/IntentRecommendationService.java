package com.greensteps.intentrecommendation.service;

import com.greensteps.intentrecommendation.dto.IntentRecommendationResponse;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class IntentRecommendationService {

  public IntentRecommendationResponse getRecommendation(String mode, BigDecimal distanceKm) {
    
    if (distanceKm == null || distanceKm.signum() < 0) {
      return new IntentRecommendationResponse(
          "none",
          "invalid distance!"
      );
    }

    if (mode == null || mode.trim().isEmpty()) {
      return new IntentRecommendationResponse(
          "none",
          "Enter a travel mode!"
      );
    }
    
    String currentMode = mode.trim().toLowerCase();
    double km = distanceKm.doubleValue();

    /*RULES:
    Recommend alternate modes of transport for all activities having a transport mode
    Base it on distance
    Give a message 
    */

    switch (currentMode) {
      case "car":
        if (km <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is the greenest for short trips!"
          );
        }
        if (km <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a cleaner option for this trip!"
          );
        }
        if (km <= 15) {
          return new IntentRecommendationResponse(
              "bus",
              "Taking the bus can reduce emissions!"
          );
        }
        return new IntentRecommendationResponse(
            "train",
            "Train travel is best for longer trips!"
        );

      case "rideshare":
        if (km <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is the greenest for short trips!"
          );
        }
        if (km <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a cleaner option for this trip!"
          );
        }
        if (km <= 15) {
          return new IntentRecommendationResponse(
              "bus",
              "Taking the bus can reduce emissions!"
          );
        }
        return new IntentRecommendationResponse(
            "train",
            "Train travel is best for longer trips!"
        );

      case "bus":
        if (km <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is the greenest for short trips!"
          );
        }
        if (km <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a cleaner option for this trip!"
          );
        }
        return new IntentRecommendationResponse(
            "train",
            "Train travel is best for longer trips!"
        );

      case "train":
        if (km <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is the greenest option for short trips!"
          );
        }
        if (km <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a cleaner option for this trip!"
          );
        }
        return new IntentRecommendationResponse(
            "none",
            "Nice choice!"
        );

      case "bike":
        if (km <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Nice! Walking is another green option!"
          );
        }
        return new IntentRecommendationResponse(
            "bike",
            "Nice! Get your exercise in!"
        );

      case "walk":
        return new IntentRecommendationResponse(
            "none",
            "Nice! Get your warm-up!"
        );

      default:
        if (km <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Nice! Get your warm-up!"
          );
        }
        if (km <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Nice! Get your exercise in!"
          );
        }
        return new IntentRecommendationResponse(
            "bus",
            "Public transport is greener!"
        );
    }
  }
}