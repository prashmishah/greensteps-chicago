package com.greensteps.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.greensteps.user.dto.UserLoginRequest;
import com.greensteps.user.dto.UserRegistrationRequest;
import com.greensteps.user.dto.UserResponse;
import com.greensteps.user.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void register_createsUser() {
    UserRegistrationRequest request = new UserRegistrationRequest();
    request.setUsername("test-user");
    request.setPassword("password");

    UserResponse response = userService.register(request);

    assertThat(response.getId()).isNotNull();
    assertThat(response.getUsername()).isEqualTo("test-user");
    assertThat(userRepository.findById(response.getId())).isPresent();
  }

  @Test
  void login_returnsUser() {
    UserRegistrationRequest request = new UserRegistrationRequest();
    request.setUsername("login-user");
    request.setPassword("password");

    userService.register(request);

    UserLoginRequest loginRequest = new UserLoginRequest();
    loginRequest.setUsername("login-user");
    loginRequest.setPassword("password");

    UserResponse response = userService.login(loginRequest);

    assertThat(response.getId()).isNotNull();
    assertThat(response.getUsername()).isEqualTo("login-user");
  }

  @Test
  void register_rejectsDuplicateUsername() {
    UserRegistrationRequest request = new UserRegistrationRequest();
    request.setUsername("duplicate-user");
    request.setPassword("password");

    userService.register(request);

    assertThatThrownBy(() -> userService.register(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Username already exists");
  }
}
