package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.StudentManagementSystem.entity.Course;
import com.StudentManagementSystem.entity.Grade;
import com.StudentManagementSystem.entity.Student;
import com.StudentManagementSystem.repository.CourseRepository;
import com.StudentManagementSystem.repository.GradeRepository;
import com.StudentManagementSystem.service.StudentService;

import java.util.List;

@Controller
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseRepository courseRepository;

    // View grades for one student
    @GetMapping("/student/{id}")
    public String viewStudentGrades(@PathVariable int id, Model model) {
        Student student = studentService.getById(id);
        List<Grade> grades = gradeRepository.findByStudent(student);

        double avg = grades.stream().mapToInt(Grade::getMarks).average().orElse(0);

        model.addAttribute("student", student);
        model.addAttribute("grades", grades);
        model.addAttribute("average", String.format("%.1f", avg));
        model.addAttribute("allCourses", courseRepository.findAll());
        return "grade-list";
    }

    // Add grade for a student
    @PostMapping("/student/{id}")
    public String addGrade(@PathVariable int id,
                            @RequestParam int courseId,
                            @RequestParam int marks,
                            RedirectAttributes redirectAttributes) {
        Student student = studentService.getById(id);
        Course course = courseRepository.findById(courseId).get();

        if (gradeRepository.existsByStudentAndCourse(student, course)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Grade already exists for this course. Delete it first to update.");
            return "redirect:/grades/student/" + id;
        }

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setMarks(marks);
        grade.setGrade(calculateGrade(marks));
        gradeRepository.save(grade);

        redirectAttributes.addFlashAttribute("successMessage", "Grade added successfully.");
        return "redirect:/grades/student/" + id;
    }

    // Delete grade
    @GetMapping("/delete/{gradeId}/student/{studentId}")
    public String deleteGrade(@PathVariable int gradeId,
                               @PathVariable int studentId,
                               RedirectAttributes redirectAttributes) {
        gradeRepository.deleteById(gradeId);
        redirectAttributes.addFlashAttribute("successMessage", "Grade deleted.");
        return "redirect:/grades/student/" + studentId;
    }

    private String calculateGrade(int marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }
}