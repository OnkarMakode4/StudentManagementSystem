package com.StudentManagementSystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.StudentManagementSystem.entity.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findAllByOrderByPostedAtDesc();
}