package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.StudentManagementSystem.entity.Course;
import com.StudentManagementSystem.repository.CourseRepository;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "course-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        return "create-course";
    }

    @PostMapping
    public String saveCourse(@ModelAttribute Course course) {
        courseRepository.save(course);
        return "redirect:/courses";
    }

    @GetMapping("/{id}")
    public String deleteCourse(@PathVariable int id) {
        courseRepository.deleteById(id);
        return "redirect:/courses";
    }
}