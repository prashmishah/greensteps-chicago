package com.greensteps.carboncalculation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.greensteps.activity.entity.Activity;
import com.greensteps.activity.repository.ActivityRepository;
import com.greensteps.carboncalculation.dto.CarbonDashboardResponse;
import com.greensteps.user.entity.User;
import com.greensteps.user.repository.UserRepository;

@Service
public class CarbonCalculationService {

  private static final int RECENT_ACTIVITY_LIMIT = 5;

  private final UserRepository userRepository;
  private final ActivityRepository activityRepository;

  /**
   * Creates the dashboard aggregation service with read access to users and activities
   */
  public CarbonCalculationService(UserRepository userRepository, ActivityRepository activityRepository) {
    this.userRepository = userRepository;
    this.activityRepository = activityRepository;
  }

  /**
   * Builds the dashboard response for one user by aggregating activity records.
   *
   */
  public CarbonDashboardResponse buildDashboard(Long userId) throws Exception {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    List<Activity> activities = activityRepository.findByUser_IdOrderByStartTimeDesc(userId);

    BigDecimal totalDistance = activities.stream()
        .map(this::distance)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalCarbon = activities.stream()
        .map(this::carbon)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    long totalActivities = activities.size();

    CarbonDashboardResponse response = new CarbonDashboardResponse();
    response.setUserId("user_" + user.getId());
    response.setName(user.getUsername());
    response.setSummary(buildSummary(totalActivities, totalDistance, totalCarbon));
    response.setByTransport(buildTransportMetrics(activities));
    response.setDaily(buildDailyMetrics(activities));
    response.setRecentActivities(buildRecentActivities(activities));
    return response;
  }

  /**
   * Computes top totals and averages shown in the dashboard summary.
   */
  private CarbonDashboardResponse.Summary buildSummary(
      long totalActivities,
      BigDecimal totalDistance,
      BigDecimal totalCarbon
  ) {
    CarbonDashboardResponse.Summary summary = new CarbonDashboardResponse.Summary();
    summary.setTotalActivities(totalActivities);
    summary.setTotalDistanceKm(round(totalDistance));
    summary.setTotalCarbonKg(round(totalCarbon));

    if (totalActivities == 0) {
      summary.setAvgCarbonKg(BigDecimal.ZERO);
      summary.setAvgDistanceKm(BigDecimal.ZERO);
    } else {
      BigDecimal count = BigDecimal.valueOf(totalActivities);
      summary.setAvgCarbonKg(round(totalCarbon.divide(count, 4, RoundingMode.HALF_UP)));
      summary.setAvgDistanceKm(round(totalDistance.divide(count, 4, RoundingMode.HALF_UP)));
    }
    return summary;
  }

  /**
   * Groups activities by transportation method and calculates totals for each mode.
   */
  private List<CarbonDashboardResponse.TransportMetric> buildTransportMetrics(List<Activity> activities) {
    Map<String, List<Activity>> byMode = activities.stream()
        .collect(Collectors.groupingBy(this::mode, LinkedHashMap::new, Collectors.toList()));

    return byMode.entrySet()
        .stream()
        .map(entry -> {
          CarbonDashboardResponse.TransportMetric metric = new CarbonDashboardResponse.TransportMetric();
          metric.setMode(entry.getKey());
          metric.setActivities(entry.getValue().size());
          metric.setDistanceKm(round(entry.getValue().stream().map(this::distance).reduce(BigDecimal.ZERO, BigDecimal::add)));
          metric.setCarbonKg(round(entry.getValue().stream().map(this::carbon).reduce(BigDecimal.ZERO, BigDecimal::add)));
          return metric;
        })
        .sorted(Comparator.comparingLong(CarbonDashboardResponse.TransportMetric::getActivities).reversed())
        .collect(Collectors.toList());
  }

  /**
   * TODO: Aggregate activities by day (date only) for dashboard charting.
   * Please reference ~/examples/example_dashboard_response.json
   *
   */
  private List<CarbonDashboardResponse.DailyMetric> buildDailyMetrics(List<Activity> activities) {
    Map<LocalDate, List<Activity>> byDay = activities.stream()
      .collect(Collectors.groupingBy(this::activityDate));
      return byDay.entrySet().stream()
      .sorted(Map.Entry.comparingByKey())
      .map(entry -> {
        LocalDate date = entry.getKey();
        List<Activity> dayActivities = entry.getValue();

        BigDecimal totalDistance = dayActivities.stream()
            .map(this::distance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCarbon = dayActivities.stream()
            .map(this::carbon)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        CarbonDashboardResponse.DailyMetric metric =
            new CarbonDashboardResponse.DailyMetric();

        metric.setDate(date);
        metric.setActivities(dayActivities.size());
        metric.setDistanceKm(round(totalDistance));
        metric.setCarbonKg(round(totalCarbon));

        return metric;
      })
      .collect(Collectors.toList());
  }

  /**
   * TODO: Return the most recent activities in a date-only shape for dashboard preview cards.
   * Please reference ~/examples/example_dashboard_response.json
   *
   */
  private List<CarbonDashboardResponse.RecentActivity> buildRecentActivities(List<Activity> activities) {
    return activities.stream()
    .sorted(Comparator.comparing(
          Activity::getStartTime,
          Comparator.nullsLast(Comparator.naturalOrder())
      ).reversed())
      .limit(RECENT_ACTIVITY_LIMIT)
      .map(activity -> {
        CarbonDashboardResponse.RecentActivity recent = new CarbonDashboardResponse.RecentActivity();

        recent.setActivityId("act_" + activity.getId());
        recent.setMode(mode(activity));
        recent.setDistanceKm(round(distance(activity)));
        recent.setCarbonKg(round(carbon(activity)));
        recent.setDate(activityDate(activity));

        // FIXME::Not available in Activity entity yet
        recent.setStartLocation(null);
        recent.setEndLocation(null);

        return recent;
      })
      .collect(Collectors.toList());
  }

  /**
   * Null distance values to zero.
   */
  private BigDecimal distance(Activity activity) {
    return activity.getDistanceKm() == null ? BigDecimal.ZERO : activity.getDistanceKm();
  }

  /**
   * Null carbon values to zero for aggregation.
   */
  private BigDecimal carbon(Activity activity) {
    return activity.getCarbonKg() == null ? BigDecimal.ZERO : activity.getCarbonKg();
  }

  /**
   * Normalizes mode values for grouping and output consistency.
   */
  private String mode(Activity activity) {
    if (activity.getMode() == null || activity.getMode().isBlank()) {
      return "UNKNOWN";
    }
    return activity.getMode().toUpperCase(Locale.ROOT);
  }

  /**
   * Resolves the activity date from start time first, then created time, then current date.
   */
  private LocalDate activityDate(Activity activity) {
    if (activity.getStartTime() != null) {
      return activity.getStartTime().toLocalDate();
    }
    if (activity.getCreatedAt() != null) {
      return activity.getCreatedAt().toLocalDate();
    }
    return LocalDate.now();
  }

  /**
   * Rounds the numeric dashboard values to two decimal places.
   */
  private BigDecimal round(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }
}
