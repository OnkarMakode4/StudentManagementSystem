package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.StudentManagementSystem.entity.User;
import com.StudentManagementSystem.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, @RequestParam String role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userService.saveUser(user);
        return "redirect:/login";
    }

    // ---------- FORGOT PASSWORD ----------

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String verifySecurityAnswer(@RequestParam String email,
                                        Model model) {
        User user = userService.findByEmail(email);
        if (user == null || user.getSecurityQuestion() == null) {
            model.addAttribute("error", "No account found with that email, or no security question set.");
            return "forgot-password";
        }
        model.addAttribute("email", email);
        model.addAttribute("securityQuestion", user.getSecurityQuestion());
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                 @RequestParam String securityAnswer,
                                 @RequestParam String newPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(email);

        if (user == null || user.getSecurityAnswer() == null
                || !user.getSecurityAnswer().equalsIgnoreCase(securityAnswer.trim())) {
            model.addAttribute("error", "Security answer is incorrect.");
            model.addAttribute("email", email);
            model.addAttribute("securityQuestion", user != null ? user.getSecurityQuestion() : "");
            return "reset-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully. Please log in.");
        return "redirect:/login";
    }

    // ---------- EDIT OWN PROFILE ----------

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String username,
                                 @RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String securityQuestion,
                                 @RequestParam(required = false) String securityAnswer,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());

        user.setUsername(username);

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        if (securityQuestion != null && !securityQuestion.isBlank()) {
            user.setSecurityQuestion(securityQuestion);
        }
        if (securityAnswer != null && !securityAnswer.isBlank()) {
            user.setSecurityAnswer(securityAnswer.trim());
        }

        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/profile";
    }
}