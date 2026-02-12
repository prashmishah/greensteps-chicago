package com.greensteps.user.controller;

import com.greensteps.user.dto.UserLoginRequest;
import com.greensteps.user.dto.UserRegistrationRequest;
import com.greensteps.user.dto.UserResponse;
import com.greensteps.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse register(@RequestBody UserRegistrationRequest request) {
    return userService.register(request);
  }

  @PostMapping("/login")
  public UserResponse login(@RequestBody UserLoginRequest request) {
    return userService.login(request);
  }
}
