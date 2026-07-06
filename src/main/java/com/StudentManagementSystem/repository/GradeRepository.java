package com.StudentManagementSystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.StudentManagementSystem.entity.Grade;
import com.StudentManagementSystem.entity.Student;
import com.StudentManagementSystem.entity.Course;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
    List<Grade> findByStudent(Student student);
    List<Grade> findByStudentAndCourse(Student student, Course course);
    boolean existsByStudentAndCourse(Student student, Course course);
}