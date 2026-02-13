package com.greensteps.activity.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ActivityResponse {

  private Long id;
  private Long userId;
  private String activityType;
  private String description;
  private OffsetDateTime startTime;
  private OffsetDateTime endTime;
  private BigDecimal distanceKm;
  private String mode;
  private BigDecimal carbonKg;
  private OffsetDateTime createdAt;

  public ActivityResponse() {
  }

  public ActivityResponse(
      Long id,
      Long userId,
      String activityType,
      String description,
      OffsetDateTime startTime,
      OffsetDateTime endTime,
      BigDecimal distanceKm,
      String mode,
      BigDecimal carbonKg,
      OffsetDateTime createdAt
  ) {
    this.id = id;
    this.userId = userId;
    this.activityType = activityType;
    this.description = description;
    this.startTime = startTime;
    this.endTime = endTime;
    this.distanceKm = distanceKm;
    this.mode = mode;
    this.carbonKg = carbonKg;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }

  public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
  }

  public BigDecimal getDistanceKm() {
    return distanceKm;
  }

  public void setDistanceKm(BigDecimal distanceKm) {
    this.distanceKm = distanceKm;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public BigDecimal getCarbonKg() {
    return carbonKg;
  }

  public void setCarbonKg(BigDecimal carbonKg) {
    this.carbonKg = carbonKg;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
