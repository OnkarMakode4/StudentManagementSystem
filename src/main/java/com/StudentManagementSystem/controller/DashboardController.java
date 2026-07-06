package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.StudentManagementSystem.entity.Course;
import com.StudentManagementSystem.entity.Student;
import com.StudentManagementSystem.repository.CourseRepository;
import com.StudentManagementSystem.service.StudentService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Student> students = studentService.getAllStudents();
        List<Course> courses = courseRepository.findAll();

        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalCourses", courses.size());

        long withPhoto = students.stream().filter(s -> s.getPhotoPath() != null && !s.getPhotoPath().isEmpty()).count();
        model.addAttribute("withPhoto", withPhoto);
        model.addAttribute("withoutPhoto", students.size() - withPhoto);

        // Gender breakdown
        Map<String, Long> genderCounts = students.stream()
                .filter(s -> s.getGender() != null && !s.getGender().isEmpty())
                .collect(Collectors.groupingBy(Student::getGender, Collectors.counting()));

        long unspecifiedGender = students.stream()
                .filter(s -> s.getGender() == null || s.getGender().isEmpty())
                .count();
        if (unspecifiedGender > 0) {
            genderCounts.put("Not specified", unspecifiedGender);
        }

        model.addAttribute("genderLabels", genderCounts.keySet());
        model.addAttribute("genderValues", genderCounts.values());

        // Students per course
        Map<String, Long> courseCounts = new LinkedHashMap<>();
        for (Course c : courses) {
            courseCounts.put(c.getCourseName(), (long) c.getStudents().size());
        }

        long unassigned = students.stream()
                .filter(s -> s.getCourses() == null || s.getCourses().isEmpty())
                .count();
        if (unassigned > 0) {
            courseCounts.put("Unassigned", unassigned);
        }

        model.addAttribute("courseLabels", courseCounts.keySet());
        model.addAttribute("courseValues", courseCounts.values());

        return "dashboard";
    }
}