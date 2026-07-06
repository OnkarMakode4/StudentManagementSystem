package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import com.StudentManagementSystem.entity.Course;
import com.StudentManagementSystem.entity.Timetable;
import com.StudentManagementSystem.repository.CourseRepository;
import com.StudentManagementSystem.repository.TimetableRepository;

import java.util.*;

@Controller
@RequestMapping("/timetable")
public class TimetableController {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static final List<String> DAYS = Arrays.asList(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    );

    @GetMapping
    @Transactional(readOnly = true)
    public String viewTimetable(Model model) {

        // Fetch all slots with course eagerly loaded via JOIN FETCH
        List<Timetable> allSlots = timetableRepository.findAllWithCourse();

        // Build per-day lists
        List<Timetable> mondaySlots    = new ArrayList<>();
        List<Timetable> tuesdaySlots   = new ArrayList<>();
        List<Timetable> wednesdaySlots = new ArrayList<>();
        List<Timetable> thursdaySlots  = new ArrayList<>();
        List<Timetable> fridaySlots    = new ArrayList<>();
        List<Timetable> saturdaySlots  = new ArrayList<>();

        for (Timetable slot : allSlots) {
            String day = slot.getDayOfWeek();
            if (day == null) continue;
            switch (day.trim()) {
                case "Monday"    -> mondaySlots.add(slot);
                case "Tuesday"   -> tuesdaySlots.add(slot);
                case "Wednesday" -> wednesdaySlots.add(slot);
                case "Thursday"  -> thursdaySlots.add(slot);
                case "Friday"    -> fridaySlots.add(slot);
                case "Saturday"  -> saturdaySlots.add(slot);
            }
        }

        model.addAttribute("mondaySlots",    mondaySlots);
        model.addAttribute("tuesdaySlots",   tuesdaySlots);
        model.addAttribute("wednesdaySlots", wednesdaySlots);
        model.addAttribute("thursdaySlots",  thursdaySlots);
        model.addAttribute("fridaySlots",    fridaySlots);
        model.addAttribute("saturdaySlots",  saturdaySlots);

        model.addAttribute("allCourses", courseRepository.findAll());
        model.addAttribute("newSlot", new Timetable());

        return "timetable";
    }

    @PostMapping
    @Transactional
    public String addSlot(@RequestParam String dayOfWeek,
                           @RequestParam int courseId,
                           @RequestParam String startTime,
                           @RequestParam String endTime,
                           @RequestParam(required = false) String roomNumber,
                           RedirectAttributes redirectAttributes) {

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected course not found.");
            return "redirect:/timetable";
        }

        Timetable slot = new Timetable();
        slot.setDayOfWeek(dayOfWeek.trim());
        slot.setCourse(course);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setRoomNumber(roomNumber);

        timetableRepository.save(slot);
        redirectAttributes.addFlashAttribute("successMessage", "Timetable slot added successfully!");
        return "redirect:/timetable";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteSlot(@PathVariable int id,
                              RedirectAttributes redirectAttributes) {
        timetableRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Slot deleted.");
        return "redirect:/timetable";
    }
}