package com.greensteps.intentrecommendation.service;

import com.greensteps.intentrecommendation.dto.IntentRecommendationResponse;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class IntentRecommendationService {

  public IntentRecommendationResponse getRecommendation(String mode, BigDecimal distance) {
    String currentMode = mode == null ? "" : mode.trim().toLowerCase();
    double miles = distance == null ? 0 : distance.doubleValue();

    /*RULES:
    Recommend alternate modes of transport for all activities having a transport mode
    Base it on distance
    Give a message 
    */

    switch (currentMode) {
      case "car":
        if (miles <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is a great low-carbon option for short trips."
          );
        }
        if (miles <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a cleaner option for this distance."
          );
        }
        if (miles <= 15) {
          return new IntentRecommendationResponse(
              "bus",
              "Taking the bus can reduce emissions compared to driving."
          );
        }
        return new IntentRecommendationResponse(
            "train",
            "Train travel can be a lower-emission option for longer trips."
        );

      case "rideshare":
        if (miles <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is a great low-carbon option for short trips."
          );
        }
        if (miles <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a cleaner option for this distance."
          );
        }
        if (miles <= 15) {
          return new IntentRecommendationResponse(
              "bus",
              "The bus usually emits less CO2 per passenger than rideshare."
          );
        }
        return new IntentRecommendationResponse(
            "train",
            "Train travel can be a better option for longer trips."
        );

      case "bus":
        if (miles <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is the greenest option for very short trips."
          );
        }
        if (miles <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking can remove transport emissions for shorter trips."
          );
        }
        return new IntentRecommendationResponse(
            "train",
            "Train may be a lower-emission option for this distance."
        );

      case "train":
        if (miles <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is the greenest option for very short trips."
          );
        }
        if (miles <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a low-carbon option for short trips."
          );
        }
        return new IntentRecommendationResponse(
            "bus",
            "Bus may be a practical low-emission option here."
        );

      case "bike":
        if (miles <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is an even simpler zero-emission option for this short trip."
          );
        }
        return new IntentRecommendationResponse(
            "bike",
            "You are already using one of the greenest travel options."
        );

      case "walk":
        return new IntentRecommendationResponse(
            "walk",
            "You are already using the greenest travel option."
        );

      default:
        if (miles <= 2) {
          return new IntentRecommendationResponse(
              "walk",
              "Walking is a great low-carbon option for short trips."
          );
        }
        if (miles <= 5) {
          return new IntentRecommendationResponse(
              "bike",
              "Biking is a cleaner option for this distance."
          );
        }
        return new IntentRecommendationResponse(
            "bus",
            "Public transport is often a lower-emission alternative."
        );
    }
  }
}