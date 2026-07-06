package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import com.StudentManagementSystem.entity.Course;
import com.StudentManagementSystem.entity.Student;
import com.StudentManagementSystem.repository.CourseRepository;
import com.StudentManagementSystem.service.StudentService;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseRepository courseRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final int PAGE_SIZE = 5;

    @GetMapping
    public String listStudents(@RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Student> studentPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            studentPage = studentService.searchStudents(keyword, pageable);
        } else {
            studentPage = studentService.getAllStudents(pageable);
        }

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        return "student";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("allCourses", courseRepository.findAll());
        return "create-student";
    }

    @PostMapping
    public String saveStudent(@Valid @ModelAttribute("student") Student student,
                               BindingResult result,
                               @RequestParam(value = "photo", required = false) MultipartFile photo,
                               @RequestParam(value = "courseIds", required = false) List<Integer> courseIds,
                               Model model,
                               RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("allCourses", courseRepository.findAll());
            return "create-student";
        }
        if (photo != null && !photo.isEmpty()) {
            student.setPhotoPath(savePhoto(photo));
        }
        student.setCourses(resolveCourses(courseIds));
        studentService.saveStudent(student);
        redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        model.addAttribute("student", studentService.getById(id));
        model.addAttribute("allCourses", courseRepository.findAll());
        return "edit_student";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable int id,
                                 @Valid @ModelAttribute("student") Student student,
                                 BindingResult result,
                                 @RequestParam(value = "photo", required = false) MultipartFile photo,
                                 @RequestParam(value = "courseIds", required = false) List<Integer> courseIds,
                                 Model model,
                                 RedirectAttributes redirectAttributes) throws IOException {
        student.setId(id);
        if (result.hasErrors()) {
            model.addAttribute("allCourses", courseRepository.findAll());
            return "edit_student";
        }
        if (photo != null && !photo.isEmpty()) {
            student.setPhotoPath(savePhoto(photo));
        } else {
            student.setPhotoPath(studentService.getById(id).getPhotoPath());
        }
        student.setCourses(resolveCourses(courseIds));
        studentService.saveStudent(student);
        redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        return "redirect:/students";
    }

    @GetMapping("/{id}")
    public String deleteStudent(@PathVariable int id, RedirectAttributes redirectAttributes) {
        studentService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        return "redirect:/students";
    }

    // ---------- VIEW STUDENT DETAIL ----------
    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable int id, Model model) {
        model.addAttribute("student", studentService.getById(id));
        return "student-detail";
    }

    // ---------- STUDENT ID CARD ----------
    @GetMapping("/idcard/{id}")
    public String studentIdCard(@PathVariable int id, Model model) {
        model.addAttribute("student", studentService.getById(id));
        return "student-idcard";
    }

    // ---------- EXPORT CSV ----------
    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=students.csv");

        PrintWriter writer = response.getWriter();
        writer.println("First Name,Last Name,Email,Gender,Courses");

        for (Student s : studentService.getAllStudents()) {
            String courses = s.getCourses().stream()
                    .map(c -> c.getCourseName())
                    .reduce("", (a, b) -> a.isEmpty() ? b : a + " | " + b);
            writer.println(s.getFirstName() + "," + s.getLastName() + ","
                    + s.getEmail() + "," + s.getGender() + "," + courses);
        }
        writer.flush();
    }

    private String savePhoto(MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path target = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    private Set<Course> resolveCourses(List<Integer> courseIds) {
        Set<Course> courses = new HashSet<>();
        if (courseIds != null) {
            courses.addAll(courseRepository.findAllById(courseIds));
        }
        return courses;
    }
}