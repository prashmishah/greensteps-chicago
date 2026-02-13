package com.greensteps.user.dto;

public class UserRegistrationRequest {

  private String username;
  private String password;

  public UserRegistrationRequest() {
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
