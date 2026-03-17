package com.greensteps.intentrecommendation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.greensteps.intentrecommendation.dto.IntentRecommendationResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntentRecommendationServiceTest {

    private IntentRecommendationService intentRecommendationService;

    @BeforeEach
    void setUp() {
        intentRecommendationService = new IntentRecommendationService();
    }

    @Test
    void getRecommendation_nullDistance_returnsInvalidDistanceMessage() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("car", null);

        assertThat(response.getAlternateMode()).isEqualTo("none");
        assertThat(response.getMessage()).isEqualTo("invalid distance!");
    }

    @Test
    void getRecommendation_negativeDistance_returnsInvalidDistanceMessage() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("car", new BigDecimal("-1.00"));

        assertThat(response.getAlternateMode()).isEqualTo("none");
        assertThat(response.getMessage()).isEqualTo("invalid distance!");
    }

    @Test
    void getRecommendation_nullMode_returnsEnterTravelModeMessage() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation(null, new BigDecimal("2.00"));

        assertThat(response.getAlternateMode()).isEqualTo("none");
        assertThat(response.getMessage()).isEqualTo("Enter a travel mode!");
    }

    @Test
    void getRecommendation_blankMode_returnsEnterTravelModeMessage() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("   ", new BigDecimal("2.00"));

        assertThat(response.getAlternateMode()).isEqualTo("none");
        assertThat(response.getMessage()).isEqualTo("Enter a travel mode!");
    }

    @Test
    void getRecommendation_carAtTwoKm_recommendsWalk() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("car", new BigDecimal("2.00"));

        assertThat(response.getAlternateMode()).isEqualTo("walk");
        assertThat(response.getMessage()).isEqualTo("Walking is the greenest for short trips!");
    }

    @Test
    void getRecommendation_carAboveTwoAndUpToFiveKm_recommendsBike() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("car", new BigDecimal("5.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bike");
        assertThat(response.getMessage()).isEqualTo("Biking is a cleaner option for this trip!");
    }

    @Test
    void getRecommendation_carAboveFiveAndUpToFifteenKm_recommendsBus() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("car", new BigDecimal("15.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bus");
        assertThat(response.getMessage()).isEqualTo("Taking the bus can reduce emissions!");
    }

    @Test
    void getRecommendation_carAboveFifteenKm_recommendsTrain() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("car", new BigDecimal("15.01"));

        assertThat(response.getAlternateMode()).isEqualTo("train");
        assertThat(response.getMessage()).isEqualTo("Train travel is best for longer trips!");
    }

    @Test
    void getRecommendation_rideshareFollowsSameRulesAsCar() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("rideshare", new BigDecimal("6.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bus");
        assertThat(response.getMessage()).isEqualTo("Taking the bus can reduce emissions!");
    }

    @Test
    void getRecommendation_busAtShortDistance_recommendsWalk() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("bus", new BigDecimal("2.00"));

        assertThat(response.getAlternateMode()).isEqualTo("walk");
        assertThat(response.getMessage()).isEqualTo("Walking is the greenest for short trips!");
    }

    @Test
    void getRecommendation_busAtMediumDistance_recommendsBike() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("bus", new BigDecimal("4.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bike");
        assertThat(response.getMessage()).isEqualTo("Biking is a cleaner option for this trip!");
    }

    @Test
    void getRecommendation_busAtLongDistance_recommendsTrain() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("bus", new BigDecimal("6.00"));

        assertThat(response.getAlternateMode()).isEqualTo("train");
        assertThat(response.getMessage()).isEqualTo("Train travel is best for longer trips!");
    }

    @Test
    void getRecommendation_trainAtShortDistance_recommendsWalk() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("train", new BigDecimal("2.00"));

        assertThat(response.getAlternateMode()).isEqualTo("walk");
        assertThat(response.getMessage()).isEqualTo("Walking is the greenest option for short trips!");
    }

    @Test
    void getRecommendation_trainAtMediumDistance_recommendsBike() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("train", new BigDecimal("4.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bike");
        assertThat(response.getMessage()).isEqualTo("Biking is a cleaner option for this trip!");
    }

    @Test
    void getRecommendation_trainAtLongDistance_returnsNiceChoice() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("train", new BigDecimal("6.00"));

        assertThat(response.getAlternateMode()).isEqualTo("none");
        assertThat(response.getMessage()).isEqualTo("Nice choice!");
    }

    @Test
    void getRecommendation_bikeAtShortDistance_recommendsWalk() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("bike", new BigDecimal("2.00"));

        assertThat(response.getAlternateMode()).isEqualTo("walk");
        assertThat(response.getMessage()).isEqualTo("Nice! Walking is another green option!");
    }

    @Test
    void getRecommendation_bikeAtLongerDistance_keepsBike() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("bike", new BigDecimal("3.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bike");
        assertThat(response.getMessage()).isEqualTo("Nice! Get your exercise in!");
    }

    @Test
    void getRecommendation_walk_returnsNiceMessage() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("walk", new BigDecimal("1.00"));

        assertThat(response.getAlternateMode()).isEqualTo("none");
        assertThat(response.getMessage()).isEqualTo("Nice! Get your warm-up!");
    }

    @Test
    void getRecommendation_unknownModeAtShortDistance_recommendsWalk() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("scooter", new BigDecimal("2.00"));

        assertThat(response.getAlternateMode()).isEqualTo("walk");
        assertThat(response.getMessage()).isEqualTo("Nice! Get your warm-up!");
    }

    @Test
    void getRecommendation_unknownModeAtMediumDistance_recommendsBike() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("scooter", new BigDecimal("5.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bike");
        assertThat(response.getMessage()).isEqualTo("Nice! Get your exercise in!");
    }

    @Test
    void getRecommendation_unknownModeAtLongDistance_recommendsBus() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("scooter", new BigDecimal("7.00"));

        assertThat(response.getAlternateMode()).isEqualTo("bus");
        assertThat(response.getMessage()).isEqualTo("Public transport is greener!");
    }

    @Test
    void getRecommendation_trimsAndIgnoresCaseInMode() {
        IntentRecommendationResponse response =
                intentRecommendationService.getRecommendation("  CAR  ", new BigDecimal("1.00"));

        assertThat(response.getAlternateMode()).isEqualTo("walk");
        assertThat(response.getMessage()).isEqualTo("Walking is the greenest for short trips!");
    }
}