package com.greensteps.activity.service;

import com.greensteps.activity.dto.ActivityRequest;
import com.greensteps.activity.dto.ActivityResponse;
import com.greensteps.activity.entity.Activity;
import com.greensteps.activity.repository.ActivityRepository;
import com.greensteps.user.entity.User;
import com.greensteps.user.repository.UserRepository;
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
    activity.setCarbonKg(request.getCarbonKg());

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
}
