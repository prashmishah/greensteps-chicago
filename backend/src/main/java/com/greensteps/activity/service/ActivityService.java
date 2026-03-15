package com.greensteps.activity.service;

import com.greensteps.activity.dto.ActivityRequest;
import com.greensteps.activity.dto.ActivityResponse;
import com.greensteps.activity.entity.Activity;
import com.greensteps.activity.repository.ActivityRepository;
import com.greensteps.user.entity.User;
import com.greensteps.user.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ActivityService {

  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;

  public ActivityService(ActivityRepository activityRepository, UserRepository userRepository) {
    this.activityRepository = activityRepository;
    this.userRepository = userRepository;
  }

  public ActivityResponse create(ActivityRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Activity activity = new Activity();
    activity.setUser(user);
    activity.setActivityType(request.getActivityType());
    activity.setDescription(request.getDescription());
    activity.setStartTime(request.getStartTime());
    activity.setEndTime(request.getEndTime());
    activity.setDistanceKm(request.getDistanceKm());
    activity.setMode(request.getMode());
    activity.setCarbonKg(estimateCarbonKg(request));

    Activity saved = activityRepository.save(activity);
    return toResponse(saved);
  }

  public List<ActivityResponse> listByUser(Long userId) {
    return activityRepository.findByUser_IdOrderByStartTimeDesc(userId)
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  private ActivityResponse toResponse(Activity activity) {
    return new ActivityResponse(
        activity.getId(),
        activity.getUser().getId(),
        activity.getActivityType(),
        activity.getDescription(),
        activity.getStartTime(),
        activity.getEndTime(),
        activity.getDistanceKm(),
        activity.getMode(),
        activity.getCarbonKg(),
        activity.getCreatedAt()
    );
  }

  private BigDecimal estimateCarbonKg(ActivityRequest request) {
    // the following calculations per category follow the frontend calculation guidelines
    String type = request.getActivityType() == null ? "" : request.getActivityType().trim().toLowerCase();
    String mode = request.getMode() == null ? "" : request.getMode().trim().toLowerCase();
    BigDecimal distanceKm = request.getDistanceKm() == null ? BigDecimal.ZERO : request.getDistanceKm();

    if (type.startsWith("commute")) {
      return round(distanceKm.multiply(perKmFactor(mode)));
    }

    if (type.equals("dining")) {
      // by default match frontend when recommendations are not provided
      BigDecimal base = new BigDecimal("1.6"); // lunch
      BigDecimal factor = new BigDecimal("0.85"); // vegetarian
      return round(base.multiply(factor));
    }

    if (type.equals("grocery") || type.equals("shopping")) {
      BigDecimal base = new BigDecimal("2.0");
      return round(base);
    }

    if (type.equals("gym") || type.equals("leisure")) {
      return round(distanceKm.multiply(perKmFactor(mode, new BigDecimal("0.2"))));
    }

    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal perKmFactor(String mode) {
    return perKmFactor(mode, new BigDecimal("0.35"));
  }

  private BigDecimal perKmFactor(String mode, BigDecimal defaultFactor) {
    switch (mode) {
      case "walk":
      case "bike":
        return BigDecimal.ZERO;
      case "train":
        return new BigDecimal("0.09");
      case "bus":
        return new BigDecimal("0.12");
      case "car":
        return new BigDecimal("0.35");
      case "rideshare":
        return new BigDecimal("0.40");
      default:
        return defaultFactor;
    }
  }

  private BigDecimal round(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }
}
