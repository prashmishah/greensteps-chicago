package com.greensteps.activity.controller;

import com.greensteps.activity.dto.ActivityRequest;
import com.greensteps.activity.dto.ActivityResponse;
import com.greensteps.activity.service.ActivityService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    activityService.delete(id);
  }
}