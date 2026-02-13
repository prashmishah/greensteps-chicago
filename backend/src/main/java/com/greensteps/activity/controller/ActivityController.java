package com.greensteps.activity.controller;

import com.greensteps.activity.dto.ActivityRequest;
import com.greensteps.activity.dto.ActivityResponse;
import com.greensteps.activity.service.ActivityService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

  private final ActivityService activityService;

  public ActivityController(ActivityService activityService) {
    this.activityService = activityService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ActivityResponse create(@RequestBody ActivityRequest request) {
    return activityService.create(request);
  }

  @GetMapping
  public List<ActivityResponse> list(@RequestParam("userId") Long userId) {
    return activityService.listByUser(userId);
  }
}
