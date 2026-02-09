package com.greensteps.activity.entity;

import com.greensteps.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "activities")
public class Activity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "activity_type", nullable = false, length = 50)
  private String activityType;

  @Column(length = 255)
  private String description;

  @Column(name = "start_time")
  private OffsetDateTime startTime;

  @Column(name = "end_time")
  private OffsetDateTime endTime;

  @Column(name = "distance_km", precision = 10, scale = 2)
  private BigDecimal distanceKm;

  @Column(length = 50)
  private String mode;

  @Column(name = "carbon_kg", nullable = false, precision = 12, scale = 4)
  private BigDecimal carbonKg = BigDecimal.ZERO;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  private void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
    if (carbonKg == null) {
      carbonKg = BigDecimal.ZERO;
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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
