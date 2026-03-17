package com.greensteps.carboncalculation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.greensteps.activity.dto.ActivityRequest;
import com.greensteps.activity.dto.ActivityResponse;
import com.greensteps.activity.service.ActivityService;
import com.greensteps.carboncalculation.dto.CarbonDashboardResponse;
import com.greensteps.user.entity.User;
import com.greensteps.user.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
class CarbonCalculationServiceTest {

  @Autowired
  private CarbonCalculationService carbonCalculationService;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void buildDashboard() throws Exception {
    User user = createUser("carbon-dashboard-user-" + System.nanoTime());

    BigDecimal distance = new BigDecimal("10.00");
    String mode = "car";
    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 25, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            distance,
            mode,
            new BigDecimal("2.00"));

    CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());

    assertThat(res).isNotNull();
    assertThat(res.getUserId()).isEqualTo("user_" + user.getId());
    assertThat(res.getName()).isEqualTo(user.getUsername());

    assertThat(res.getSummary()).isNotNull();
    assertThat(res.getByTransport()).isNotNull();
    assertThat(res.getDaily()).isNotNull();
    assertThat(res.getRecentActivities()).isNotNull();

    assertThat(res.getSummary().getTotalActivities()).isEqualTo(1);
    assertThat(res.getSummary().getTotalDistanceKm()).isEqualByComparingTo(distance);
    assertThat(res.getSummary().getTotalCarbonKg()).isEqualByComparingTo(expectedCarbon("commute_work", distance, mode));
  }

  @Test
  void buildDashboard_userNotFound_throwsNotFound() {
    Long missingUserId = 99999999L;

    assertThatThrownBy(() -> carbonCalculationService.buildDashboard(missingUserId))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode().value()).isEqualTo(404);
            });
  }

  @Test
  void buildDashboard_emptyActivities_returnsZeroSummary() throws Exception {
    User user = createUser("carbon-empty-user-" + System.nanoTime());

    CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());

    assertThat(res).isNotNull();
    assertThat(res.getSummary()).isNotNull();

    assertThat(res.getSummary().getTotalActivities()).isEqualTo(0);
    assertThat(res.getSummary().getTotalDistanceKm()).isEqualByComparingTo(new BigDecimal("0.00"));
    assertThat(res.getSummary().getTotalCarbonKg()).isEqualByComparingTo(new BigDecimal("0.00"));
    assertThat(res.getSummary().getAvgDistanceKm()).isEqualByComparingTo(new BigDecimal("0.00"));
    assertThat(res.getSummary().getAvgCarbonKg()).isEqualByComparingTo(new BigDecimal("0.00"));

    assertThat(res.getByTransport()).isNotNull().isEmpty();
    assertThat(res.getDaily()).isNotNull().isEmpty();
    assertThat(res.getRecentActivities()).isNotNull().isEmpty();
  }

  @Test
  void buildDashboard_groupsByTransportMode() throws Exception {
    User user = createUser("carbon-transport-user-" + System.nanoTime());

    BigDecimal carDistance1 = new BigDecimal("5.00");
    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 25, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            carDistance1,
            "car",
            new BigDecimal("1.50"));

    BigDecimal carDistance2 = new BigDecimal("7.00");
    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 26, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            carDistance2,
            "car",
            new BigDecimal("2.00"));


    BigDecimal bikeDistance = new BigDecimal("3.00");
    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 27, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            bikeDistance,
            "bike",
            new BigDecimal("0.20"));

    CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());

    assertThat(res.getByTransport()).hasSize(2);


    CarbonDashboardResponse.TransportMetric m1 = res.getByTransport().get(0);
    CarbonDashboardResponse.TransportMetric m2 = res.getByTransport().get(1);

    assertThat(m1.getMode()).isEqualTo("CAR");
    assertThat(m1.getActivities()).isEqualTo(2);
    assertThat(m1.getDistanceKm()).isEqualByComparingTo(new BigDecimal("12.00"));
    assertThat(m1.getCarbonKg()).isEqualByComparingTo(
        expectedCarbon("commute_work", carDistance1, "car")
            .add(expectedCarbon("commute_work", carDistance2, "car"))
            .setScale(2, RoundingMode.HALF_UP)
    );

    assertThat(m2.getMode()).isEqualTo("BIKE");
    assertThat(m2.getActivities()).isEqualTo(1);
    assertThat(m2.getDistanceKm()).isEqualByComparingTo(new BigDecimal("3.00"));
    assertThat(m2.getCarbonKg()).isEqualByComparingTo(expectedCarbon("commute_work", bikeDistance, "bike"));
  }

  @Test
  void buildDashboard_sortsTransportByActivityCountDesc() throws Exception {
    User user = createUser("carbon-sort-user-" + System.nanoTime());

    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 25, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            new BigDecimal("2.00"),
            "bike",
            new BigDecimal("0.10"));

    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 26, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            new BigDecimal("3.00"),
            "car",
            new BigDecimal("1.00"));
    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 27, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            new BigDecimal("4.00"),
            "car",
            new BigDecimal("1.20"));
    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 28, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            new BigDecimal("5.00"),
            "car",
            new BigDecimal("1.30"));

    CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());
    assertThat(res.getByTransport()).hasSize(2);

    assertThat(res.getByTransport().get(0).getMode()).isEqualTo("CAR");
    assertThat(res.getByTransport().get(0).getActivities()).isEqualTo(3);

    assertThat(res.getByTransport().get(1).getMode()).isEqualTo("BIKE");
    assertThat(res.getByTransport().get(1).getActivities()).isEqualTo(1);
  }

  @Test
  void buildDashboard_buildsDailyMetrics_dateOnly() throws Exception {
    User user = createUser("carbon-daily-user-" + System.nanoTime());

    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 26, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            new BigDecimal("9.66"),
            "car",
            new BigDecimal("2.1000"));

    createActivity(user.getId(),
            OffsetDateTime.of(2026, 2, 26, 12, 0, 0, 0, ZoneOffset.ofHours(-5)),
            null,
            "car",
            new BigDecimal("1.3600"));

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
    assertThat(d1.getCarbonKg()).isEqualByComparingTo(expectedCarbon("commute_work", new BigDecimal("9.66"), "car"));

    CarbonDashboardResponse.DailyMetric d2 = res.getDaily().get(1);
    assertThat(d2.getDate()).isEqualTo(LocalDate.parse("2026-02-27"));
    assertThat(d2.getActivities()).isEqualTo(1);
    assertThat(d2.getDistanceKm()).isEqualByComparingTo(new BigDecimal("11.27"));
    assertThat(d2.getCarbonKg()).isEqualByComparingTo(expectedCarbon("commute_work", new BigDecimal("11.27"), "car"));
  }

  @Test
  void buildDashboard_buildsRecentActivities_withoutTime() throws Exception {
    User user = createUser("carbon-recent-user-" + System.nanoTime());

    BigDecimal a1Distance = new BigDecimal("1.234");
    ActivityResponse a1 = createActivityReturn(user.getId(),
            OffsetDateTime.of(2026, 2, 25, 9, 0, 0, 0, ZoneOffset.ofHours(-5)),
            a1Distance,
            "car",
            new BigDecimal("2.3456"));

    BigDecimal a2Distance = new BigDecimal("9.666");
    ActivityResponse a2 = createActivityReturn(user.getId(),
            OffsetDateTime.of(2026, 2, 26, 10, 0, 0, 0, ZoneOffset.ofHours(-5)),
            a2Distance,
            "car",
            new BigDecimal("1.3600"));

    BigDecimal a3Distance = new BigDecimal("11.270");
    ActivityResponse a3 = createActivityReturn(user.getId(),
            OffsetDateTime.of(2026, 2, 27, 8, 0, 0, 0, ZoneOffset.ofHours(-5)),
            a3Distance,
            "car",
            new BigDecimal("2.4500"));

    CarbonDashboardResponse res = carbonCalculationService.buildDashboard(user.getId());
    List<CarbonDashboardResponse.RecentActivity> recent = res.getRecentActivities();

    assertThat(recent).isNotNull();
    assertThat(recent).hasSize(3);

    CarbonDashboardResponse.RecentActivity r1 = recent.get(0);
    assertThat(r1.getActivityId()).isEqualTo("act_" + a3.getId());
    assertThat(r1.getMode()).isEqualTo("CAR");
    assertThat(r1.getDate()).isEqualTo(LocalDate.parse("2026-02-27"));
    assertThat(r1.getDistanceKm()).isEqualByComparingTo(new BigDecimal("11.27"));
    assertThat(r1.getCarbonKg()).isEqualByComparingTo(expectedCarbon("commute_work", a3Distance, "car"));
    assertThat(r1.getStartLocation()).isNull();
    assertThat(r1.getEndLocation()).isNull();

    CarbonDashboardResponse.RecentActivity r2 = recent.get(1);
    assertThat(r2.getActivityId()).isEqualTo("act_" + a2.getId());
    assertThat(r2.getDate()).isEqualTo(LocalDate.parse("2026-02-26"));
    assertThat(r2.getDistanceKm()).isEqualByComparingTo(new BigDecimal("9.67"));
    assertThat(r2.getCarbonKg()).isEqualByComparingTo(expectedCarbon("commute_work", a2Distance, "car"));

    CarbonDashboardResponse.RecentActivity r3 = recent.get(2);
    assertThat(r3.getActivityId()).isEqualTo("act_" + a1.getId());
    assertThat(r3.getDate()).isEqualTo(LocalDate.parse("2026-02-25"));
    assertThat(r3.getDistanceKm()).isEqualByComparingTo(new BigDecimal("1.23"));
    assertThat(r3.getCarbonKg()).isEqualByComparingTo(expectedCarbon("commute_work", a1Distance, "car"));
  }

  @Test
  void buildDashboard_limitsRecentActivities_toConfiguredLimit() throws Exception {
    User user = createUser("carbon-recent-limit-user-" + System.nanoTime());

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

    assertThat(recent.get(0).getDate()).isEqualTo(LocalDate.parse("2026-02-25"));
    assertThat(recent.get(4).getDate()).isEqualTo(LocalDate.parse("2026-02-21"));
    assertThat(recent).noneMatch(r -> r.getDate().equals(LocalDate.parse("2026-02-20")));
  }

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

  private BigDecimal expectedCarbon(String activityType, BigDecimal distanceKm, String mode) {
    BigDecimal km = distanceKm == null ? BigDecimal.ZERO : distanceKm;

    String type = activityType == null ? "" : activityType.trim().toLowerCase();
    String m = mode == null ? "" : mode.trim().toLowerCase();

    if (type.startsWith("commute")) {
      return round(km.multiply(perKm(m)));
    }

    if (type.equals("gym") || type.equals("leisure")) {
      return round(km.multiply(perKm(m, new BigDecimal("0.2"))));
    }

    if (type.equals("dining")) {
      BigDecimal base = new BigDecimal("1.6");
      BigDecimal factor = new BigDecimal("0.85");
      return round(base.multiply(factor));
    }

    if (type.equals("grocery") || type.equals("shopping")) {
      return round(new BigDecimal("2.0"));
    }

    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal perKm(String mode) {
    return perKm(mode, new BigDecimal("0.35"));
  }

  private BigDecimal perKm(String mode, BigDecimal defaultFactor) {
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
