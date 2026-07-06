package com.StudentManagementSystem.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.StudentManagementSystem.entity.Attendance;
import com.StudentManagementSystem.entity.Student;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByDate(LocalDate date);
    boolean existsByStudentAndDate(Student student, LocalDate date);
    List<Attendance> findByStudentAndStatus(Student student, String status);
}