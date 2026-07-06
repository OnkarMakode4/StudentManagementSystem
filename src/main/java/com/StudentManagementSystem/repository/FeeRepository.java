package com.StudentManagementSystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.StudentManagementSystem.entity.Fee;
import com.StudentManagementSystem.entity.Student;

public interface FeeRepository extends JpaRepository<Fee, Integer> {
    List<Fee> findByStudent(Student student);
    boolean existsByStudentAndTerm(Student student, String term);
}