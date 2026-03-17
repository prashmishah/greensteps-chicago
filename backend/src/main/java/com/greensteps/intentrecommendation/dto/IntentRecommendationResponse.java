package com.greensteps.intentrecommendation.dto;

//import java.math.BigDecimal;

public class IntentRecommendationResponse
{
    private String alternateMode;
    //private BigDecimal alternateCarbonkg;
    //private BigDecimal carbonkgSavings;
    private String message;


public IntentRecommendationResponse()
{
}

public IntentRecommendationResponse(
    String alternateMode,
    //BigDecimal alternateCarbonkg,
    //BigDecimal carbonkgSavings,
    String message
)
{
    this.alternateMode = alternateMode;
    //this.alternateCarbonkg = alternateCarbonkg;
    //this.carbonkgSavings = carbonkgSavings;
    this.message = message;
}

public String getAlternateMode()
{
    return alternateMode;
}

public void setAlternateMode(String alternateMode)
{
    this.alternateMode = alternateMode;
} 

public String getMessage() 
{
    return message;
}

  public void setMessage(String message) {
    this.message = message;
  }

}