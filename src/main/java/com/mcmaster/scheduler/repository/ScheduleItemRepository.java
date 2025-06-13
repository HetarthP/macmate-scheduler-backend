package com.mcmaster.scheduler.repository;

import com.mcmaster.scheduler.model.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    List<ScheduleItem> findByOwnerEmail(String ownerEmail);
}
