package com.greensteps.carboncalculation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CarbonDashboardResponse {

  private String userId;
  private String name;
  private Summary summary;
  private List<TransportMetric> byTransport;
  private List<DailyMetric> daily;
  private List<RecentActivity> recentActivities;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Summary getSummary() {
    return summary;
  }

  public void setSummary(Summary summary) {
    this.summary = summary;
  }

  public List<TransportMetric> getByTransport() {
    return byTransport;
  }

  public void setByTransport(List<TransportMetric> byTransport) {
    this.byTransport = byTransport;
  }

  public List<DailyMetric> getDaily() {
    return daily;
  }

  public void setDaily(List<DailyMetric> daily) {
    this.daily = daily;
  }

  public List<RecentActivity> getRecentActivities() {
    return recentActivities;
  }

  public void setRecentActivities(List<RecentActivity> recentActivities) {
    this.recentActivities = recentActivities;
  }

  public static class Summary {
    private long totalActivities;
    private BigDecimal totalDistanceKm;
    private BigDecimal totalCarbonKg;
    private BigDecimal avgCarbonKg;
    private BigDecimal avgDistanceKm;
    private BigDecimal carbonSavedKg;

    public long getTotalActivities() {
      return totalActivities;
    }

    public void setTotalActivities(long totalActivities) {
      this.totalActivities = totalActivities;
    }

    public BigDecimal getTotalDistanceKm() {
      return totalDistanceKm;
    }

    public void setTotalDistanceKm(BigDecimal totalDistanceKm) {
      this.totalDistanceKm = totalDistanceKm;
    }

    public BigDecimal getTotalCarbonKg() {
      return totalCarbonKg;
    }

    public void setTotalCarbonKg(BigDecimal totalCarbonKg) {
      this.totalCarbonKg = totalCarbonKg;
    }

    public BigDecimal getAvgCarbonKg() {
      return avgCarbonKg;
    }

    public void setAvgCarbonKg(BigDecimal avgCarbonKg) {
      this.avgCarbonKg = avgCarbonKg;
    }

    public BigDecimal getAvgDistanceKm() {
      return avgDistanceKm;
    }

    public void setAvgDistanceKm(BigDecimal avgDistanceKm) {
      this.avgDistanceKm = avgDistanceKm;
    }

    public BigDecimal getCarbonSavedKg() {
      return carbonSavedKg;
    }

    public void setCarbonSavedKg(BigDecimal carbonSavedKg) {
      this.carbonSavedKg = carbonSavedKg;
    }
  }

  public static class TransportMetric {
    private String mode;
    private long activities;
    private BigDecimal distanceKm;
    private BigDecimal carbonKg;

    public String getMode() {
      return mode;
    }

    public void setMode(String mode) {
      this.mode = mode;
    }

    public long getActivities() {
      return activities;
    }

    public void setActivities(long activities) {
      this.activities = activities;
    }

    public BigDecimal getDistanceKm() {
      return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
      this.distanceKm = distanceKm;
    }

    public BigDecimal getCarbonKg() {
      return carbonKg;
    }

    public void setCarbonKg(BigDecimal carbonKg) {
      this.carbonKg = carbonKg;
    }
  }

  public static class DailyMetric {
    private LocalDate date;
    private long activities;
    private BigDecimal distanceKm;
    private BigDecimal carbonKg;

    public LocalDate getDate() {
      return date;
    }

    public void setDate(LocalDate date) {
      this.date = date;
    }

    public long getActivities() {
      return activities;
    }

    public void setActivities(long activities) {
      this.activities = activities;
    }

    public BigDecimal getDistanceKm() {
      return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
      this.distanceKm = distanceKm;
    }

    public BigDecimal getCarbonKg() {
      return carbonKg;
    }

    public void setCarbonKg(BigDecimal carbonKg) {
      this.carbonKg = carbonKg;
    }
  }

  public static class RecentActivity {
    private String activityId;
    private String mode;
    private BigDecimal distanceKm;
    private BigDecimal carbonKg;
    private String startLocation;
    private String endLocation;
    private LocalDate date;

    public String getActivityId() {
      return activityId;
    }

    public void setActivityId(String activityId) {
      this.activityId = activityId;
    }

    public String getMode() {
      return mode;
    }

    public void setMode(String mode) {
      this.mode = mode;
    }

    public BigDecimal getDistanceKm() {
      return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
      this.distanceKm = distanceKm;
    }

    public BigDecimal getCarbonKg() {
      return carbonKg;
    }

    public void setCarbonKg(BigDecimal carbonKg) {
      this.carbonKg = carbonKg;
    }

    public String getStartLocation() {
      return startLocation;
    }

    public void setStartLocation(String startLocation) {
      this.startLocation = startLocation;
    }

    public String getEndLocation() {
      return endLocation;
    }

    public void setEndLocation(String endLocation) {
      this.endLocation = endLocation;
    }

    public LocalDate getDate() {
      return date;
    }

    public void setDate(LocalDate date) {
      this.date = date;
    }
  }
}
