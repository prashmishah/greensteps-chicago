package com.greensteps.carboncalculation.service;
import static org.assertj.core.api.Assertions.assertThat;
import com.greensteps.activity.dto.ActivityRequest;
import com.greensteps.activity.service.ActivityService;
import com.greensteps.activity.dto.ActivityResponse;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import com.greensteps.carboncalculation.dto.CarbonDashboardResponse;
import com.greensteps.user.entity.User;
import com.greensteps.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional

class CarbonCalculationServiceTest {

  @Autowired
  private CarbonCalculationService carbonCalculationService;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private UserRepository userRepository;
  @Disabled("TODO")
  @Test
  void buildDashboard() throws Exception {
    throw new Exception("not implemented yet");
  }

  @Disabled("TODO")
  @Test
  void buildDashboard_userNotFound_throwsNotFound() throws Exception {
    throw new Exception("not implemented yet");
  }

  @Disabled("TODO")
  @Test
  void buildDashboard_emptyActivities_returnsZeroSummary() throws Exception {
    throw new Exception("not implemented yet");
  }

  @Disabled("TODO")
  @Test
  void buildDashboard_groupsByTransportMode() throws Exception {
    throw new Exception("not implemented yet");
  }

  @Disabled("TODO")
  @Test
  void buildDashboard_sortsTransportByActivityCountDesc() throws Exception {
    throw new Exception("not implemented yet");
  }

  @Test
  void buildDashboard_buildsDailyMetrics_dateOnly() throws Exception {
    User user = createUser("carbon-daily-user");

    // 2 activities on 2026-02-26
    createActivity(user.getId(),
        OffsetDateTime.of(2026, 2, 26, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
        new BigDecimal("9.66"),
        "car",
        new BigDecimal("2.1000"));

    // null distance should be treated as 0.00 by distance()
    createActivity(user.getId(),
        OffsetDateTime.of(2026, 2, 26, 12, 0, 0, 0, ZoneOffset.ofHours(-5)),
        null,
        "car",
        new BigDecimal("1.3600"));

    // 1 activity on 2026-02-27
    createActivity(user.getId(),
        OffsetDateTime.of(2026, 2, 27, 8, 0, 0, 0, ZoneOffset.ofHours(-5)),
        new BigDecimal("11.27"),
        "car",
        new BigDecimal("2.4500"));

    CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());

    assertThat(res.getDaily()).isNotNull();
    assertThat(res.getDaily()).hasSize(2);

    CarbonDashboardResponse.DailyMetric d1 = res.getDaily().get(0);
    assertThat(d1.getDate()).isEqualTo(LocalDate.parse("2026-02-26"));
    assertThat(d1.getActivities()).isEqualTo(2);
    assertThat(d1.getDistanceKm()).isEqualByComparingTo(new BigDecimal("9.66"));
    assertThat(d1.getCarbonKg()).isEqualByComparingTo(new BigDecimal("3.46"));

    CarbonDashboardResponse.DailyMetric d2 = res.getDaily().get(1);
    assertThat(d2.getDate()).isEqualTo(LocalDate.parse("2026-02-27"));
    assertThat(d2.getActivities()).isEqualTo(1);
    assertThat(d2.getDistanceKm()).isEqualByComparingTo(new BigDecimal("11.27"));
    assertThat(d2.getCarbonKg()).isEqualByComparingTo(new BigDecimal("2.45"));
  }

  @Test
  void buildDashboard_buildsRecentActivities_withoutTime() throws Exception {
    User user = createUser("carbon-recent-user");

  ActivityResponse a1 = createActivityReturn(user.getId(),
      OffsetDateTime.of(2026, 2, 25, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
      new BigDecimal("1.234"),   // rounds to 1.23
      "car",
      new BigDecimal("2.3456")); // rounds to 2.35

  ActivityResponse a2 = createActivityReturn(user.getId(),
      OffsetDateTime.of(2026, 2, 26, 10, 0, 0, 0, ZoneOffset.ofHours(-5)),
      new BigDecimal("9.666"),   // rounds to 9.67
      "car",
      new BigDecimal("1.3600")); // rounds to 1.36

  ActivityResponse a3 = createActivityReturn(user.getId(),
      OffsetDateTime.of(2026, 2, 27, 8, 0, 0, 0, ZoneOffset.ofHours(-5)),
      new BigDecimal("11.270"),
      "car",
      new BigDecimal("2.4500"));

  CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());
  List<CarbonDashboardResponse.RecentActivity> recent = res.getRecentActivities();

  assertThat(recent).isNotNull();
  assertThat(recent).hasSize(3);

  // Service sorts by startTime desc => newest first: a3, a2, a1
  CarbonDashboardResponse.RecentActivity r1 = recent.get(0);
  assertThat(r1.getActivityId()).isEqualTo("act_" + a3.getId());
  assertThat(r1.getMode()).isEqualTo("CAR"); // mode() uppercases
  assertThat(r1.getDate()).isEqualTo(LocalDate.parse("2026-02-27")); // date-only (LocalDate)
  assertThat(r1.getDistanceKm()).isEqualByComparingTo(new BigDecimal("11.27"));
  assertThat(r1.getCarbonKg()).isEqualByComparingTo(new BigDecimal("2.45"));
  assertThat(r1.getStartLocation()).isNull();
  assertThat(r1.getEndLocation()).isNull();

  CarbonDashboardResponse.RecentActivity r2 = recent.get(1);
  assertThat(r2.getActivityId()).isEqualTo("act_" + a2.getId());
  assertThat(r2.getDate()).isEqualTo(LocalDate.parse("2026-02-26"));
  assertThat(r2.getDistanceKm()).isEqualByComparingTo(new BigDecimal("9.67"));
  assertThat(r2.getCarbonKg()).isEqualByComparingTo(new BigDecimal("1.36"));

  CarbonDashboardResponse.RecentActivity r3 = recent.get(2);
  assertThat(r3.getActivityId()).isEqualTo("act_" + a1.getId());
  assertThat(r3.getDate()).isEqualTo(LocalDate.parse("2026-02-25"));
  assertThat(r3.getDistanceKm()).isEqualByComparingTo(new BigDecimal("1.23"));
  assertThat(r3.getCarbonKg()).isEqualByComparingTo(new BigDecimal("2.35"));
  }

  @Test
  void buildDashboard_limitsRecentActivities_toConfiguredLimit() throws Exception {
    User user = createUser("carbon-recent-limit-user");

  // Create 6 activities on consecutive days (newest should be kept, oldest dropped)
  for (int day = 20; day <= 25; day++) {
    createActivityReturn(user.getId(),
        OffsetDateTime.of(2026, 2, day, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
        new BigDecimal("1.00"),
        "car",
        new BigDecimal("1.00"));
  }

  CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());
  List<CarbonDashboardResponse.RecentActivity> recent = res.getRecentActivities();

  assertThat(recent).isNotNull();
  assertThat(recent).hasSize(5);

  // Newest first => should include 2026-02-25 down to 2026-02-21, and exclude 2026-02-20
  assertThat(recent.get(0).getDate()).isEqualTo(LocalDate.parse("2026-02-25"));
  assertThat(recent.get(4).getDate()).isEqualTo(LocalDate.parse("2026-02-21"));
  assertThat(recent).noneMatch(r -> r.getDate().equals(LocalDate.parse("2026-02-20")));
  }

   // ---- helpers ----
  private User createUser(String username) {
    User user = new User();
    user.setUsername(username);
    user.setPasswordHash("password");
    return userRepository.save(user);
  }

  private void createActivity(
      Long userId,
      OffsetDateTime startTime,
      BigDecimal distanceKm,
      String mode,
      BigDecimal carbonKg
  ) {
    ActivityRequest request = new ActivityRequest();
    request.setUserId(userId);
    request.setActivityType("commute_work");
    request.setDescription("test");
    request.setStartTime(startTime);
    request.setEndTime(startTime.plusMinutes(10));
    request.setDistanceKm(distanceKm);
    request.setMode(mode);
    request.setCarbonKg(carbonKg);
    activityService.create(request);
  }
  private ActivityResponse createActivityReturn(
    Long userId,
    OffsetDateTime startTime,
    BigDecimal distanceKm,
    String mode,
    BigDecimal carbonKg
) {
  ActivityRequest request = new ActivityRequest();
  request.setUserId(userId);
  request.setActivityType("commute_work");
  request.setDescription("test");
  request.setStartTime(startTime);
  request.setEndTime(startTime.plusMinutes(10));
  request.setDistanceKm(distanceKm);
  request.setMode(mode);
  request.setCarbonKg(carbonKg);
  return activityService.create(request);
}
}
