package com.StudentManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.StudentManagementSystem.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
}