package com.StudentManagementSystem.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.StudentManagementSystem.entity.Student;

public interface StudentService {
    List<Student> getAllStudents();
    Student saveStudent(Student student);
    Student getById(int id);
    void deleteById(int id);
    List<Student> searchStudents(String keyword);

    Page<Student> getAllStudents(Pageable pageable);
    Page<Student> searchStudents(String keyword, Pageable pageable);
}