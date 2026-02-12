package com.greensteps.user.service;

import com.greensteps.user.dto.UserLoginRequest;
import com.greensteps.user.dto.UserRegistrationRequest;
import com.greensteps.user.dto.UserResponse;
import com.greensteps.user.entity.User;
import com.greensteps.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserResponse register(UserRegistrationRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPasswordHash(request.getPassword()); // TODO: hash before storing
    User saved = userRepository.save(user);
    return toResponse(saved);
  }

  public UserResponse login(UserLoginRequest request) {
    User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

    if (!user.getPasswordHash().equals(request.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    return toResponse(user);
  }

  public UserResponse toResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getLastAccessedAt(),
        user.getCreatedAt()
    );
  }
}
