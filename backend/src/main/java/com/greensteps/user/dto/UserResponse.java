package com.greensteps.user.dto;

import java.time.OffsetDateTime;

public class UserResponse {

  private Long id;
  private String username;
  private OffsetDateTime lastAccessedAt;
  private OffsetDateTime createdAt;

  public UserResponse() {
  }

  public UserResponse(Long id, String username, OffsetDateTime lastAccessedAt, OffsetDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.lastAccessedAt = lastAccessedAt;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public OffsetDateTime getLastAccessedAt() {
    return lastAccessedAt;
  }

  public void setLastAccessedAt(OffsetDateTime lastAccessedAt) {
    this.lastAccessedAt = lastAccessedAt;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
