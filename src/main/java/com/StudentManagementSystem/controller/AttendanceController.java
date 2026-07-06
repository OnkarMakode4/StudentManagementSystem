package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.StudentManagementSystem.entity.Attendance;
import com.StudentManagementSystem.entity.Student;
import com.StudentManagementSystem.repository.AttendanceRepository;
import com.StudentManagementSystem.service.StudentService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentService studentService;

    // Mark attendance for all students on a date
    @GetMapping
    public String showMarkAttendance(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("today", LocalDate.now().toString());
        return "mark-attendance";
    }

    @PostMapping
    public String saveAttendance(@RequestParam String date,
                                  @RequestParam(required = false) List<Integer> presentIds,
                                  RedirectAttributes redirectAttributes) {
        LocalDate attendanceDate = LocalDate.parse(date);
        List<Student> allStudents = studentService.getAllStudents();

        for (Student student : allStudents) {
            if (attendanceRepository.existsByStudentAndDate(student, attendanceDate)) {
                continue; // skip if already marked for this date
            }
            String status = (presentIds != null && presentIds.contains(student.getId()))
                    ? "PRESENT" : "ABSENT";
            attendanceRepository.save(new Attendance(student, attendanceDate, status));
        }

        redirectAttributes.addFlashAttribute("successMessage", "Attendance marked for " + date);
        return "redirect:/attendance";
    }

    // View attendance for one student
    @GetMapping("/student/{id}")
    public String viewStudentAttendance(@PathVariable int id, Model model) {
        Student student = studentService.getById(id);
        List<Attendance> records = attendanceRepository.findByStudent(student);

        long present = records.stream().filter(a -> a.getStatus().equals("PRESENT")).count();
        long absent = records.stream().filter(a -> a.getStatus().equals("ABSENT")).count();
        double percentage = records.isEmpty() ? 0 : (present * 100.0 / records.size());

        model.addAttribute("student", student);
        model.addAttribute("records", records);
        model.addAttribute("present", present);
        model.addAttribute("absent", absent);
        model.addAttribute("percentage", String.format("%.1f", percentage));
        return "attendance-list";
    }

    // Delete one attendance record
    @GetMapping("/delete/{id}")
    public String deleteAttendance(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Attendance a = attendanceRepository.findById(id).get();
        int studentId = a.getStudent().getId();
        attendanceRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Attendance record deleted.");
        return "redirect:/attendance/student/" + studentId;
    }
}