package com.greensteps.activity.repository;

import com.greensteps.activity.entity.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
  List<Activity> findByUser_IdOrderByStartTimeDesc(Long userId);
}
