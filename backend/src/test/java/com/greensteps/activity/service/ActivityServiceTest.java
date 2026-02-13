package com.greensteps.activity.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.greensteps.activity.dto.ActivityRequest;
import com.greensteps.activity.dto.ActivityResponse;
import com.greensteps.user.entity.User;
import com.greensteps.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ActivityServiceTest {

  @Autowired
  private ActivityService activityService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void create_and_listByUser_returnsActivity() {
    User user = new User();
    user.setUsername("activity-user");
    user.setPasswordHash("password");
    user = userRepository.save(user);

    ActivityRequest request = new ActivityRequest();
    request.setUserId(user.getId());
    request.setActivityType("commute_work");
    request.setDescription("Morning commute");
    request.setStartTime(OffsetDateTime.now());
    request.setEndTime(OffsetDateTime.now().plusMinutes(30));
    request.setDistanceKm(new BigDecimal("5.50"));
    request.setMode("car");
    request.setCarbonKg(new BigDecimal("2.35"));

    ActivityResponse created = activityService.create(request);

    assertThat(created.getId()).isNotNull();
    assertThat(created.getUserId()).isEqualTo(user.getId());
    assertThat(created.getActivityType()).isEqualTo("commute_work");

    List<ActivityResponse> results = activityService.listByUser(user.getId());

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(created.getId());
  }
}
