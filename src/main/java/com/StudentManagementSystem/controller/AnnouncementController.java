package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.StudentManagementSystem.entity.Announcement;
import com.StudentManagementSystem.repository.AnnouncementRepository;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @GetMapping
    public String listAnnouncements(Model model) {
        model.addAttribute("announcements", announcementRepository.findAllByOrderByPostedAtDesc());
        model.addAttribute("newAnnouncement", new Announcement());
        return "announcements";
    }

    @PostMapping
    public String postAnnouncement(@ModelAttribute Announcement announcement,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        announcement.setPostedBy(authentication.getName());
        announcement.setPostedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement posted successfully!");
        return "redirect:/announcements";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable int id,
                                      RedirectAttributes redirectAttributes) {
        announcementRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement deleted.");
        return "redirect:/announcements";
    }
}