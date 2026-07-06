package com.StudentManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.StudentManagementSystem.entity.Fee;
import com.StudentManagementSystem.entity.Student;
import com.StudentManagementSystem.repository.FeeRepository;
import com.StudentManagementSystem.service.StudentService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/fees")
public class FeeController {

    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private StudentService studentService;

    // View fees for one student
    @GetMapping("/student/{id}")
    public String viewStudentFees(@PathVariable int id, Model model) {
        Student student = studentService.getById(id);
        List<Fee> fees = feeRepository.findByStudent(student);

        double totalDue = fees.stream().mapToDouble(Fee::getAmountDue).sum();
        double totalPaid = fees.stream().mapToDouble(Fee::getAmountPaid).sum();
        double totalBalance = totalDue - totalPaid;

        model.addAttribute("student", student);
        model.addAttribute("fees", fees);
        model.addAttribute("totalDue", String.format("%.2f", totalDue));
        model.addAttribute("totalPaid", String.format("%.2f", totalPaid));
        model.addAttribute("totalBalance", String.format("%.2f", totalBalance));
        return "fee-list";
    }

    // Add fee record
    @PostMapping("/student/{id}")
    public String addFee(@PathVariable int id,
                          @RequestParam String term,
                          @RequestParam double amountDue,
                          @RequestParam double amountPaid,
                          @RequestParam(required = false) String paymentDate,
                          RedirectAttributes redirectAttributes) {

        Student student = studentService.getById(id);

        if (feeRepository.existsByStudentAndTerm(student, term)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Fee record for this term already exists. Delete it first to update.");
            return "redirect:/fees/student/" + id;
        }

        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setTerm(term);
        fee.setAmountDue(amountDue);
        fee.setAmountPaid(amountPaid);

        if (paymentDate != null && !paymentDate.isBlank()) {
            fee.setPaymentDate(LocalDate.parse(paymentDate));
        }

        if (amountPaid >= amountDue) {
            fee.setStatus("PAID");
        } else if (amountPaid > 0) {
            fee.setStatus("PARTIAL");
        } else {
            fee.setStatus("UNPAID");
        }

        feeRepository.save(fee);
        redirectAttributes.addFlashAttribute("successMessage", "Fee record added successfully.");
        return "redirect:/fees/student/" + id;
    }

    // Delete fee record
    @GetMapping("/delete/{feeId}/student/{studentId}")
    public String deleteFee(@PathVariable int feeId,
                             @PathVariable int studentId,
                             RedirectAttributes redirectAttributes) {
        feeRepository.deleteById(feeId);
        redirectAttributes.addFlashAttribute("successMessage", "Fee record deleted.");
        return "redirect:/fees/student/" + studentId;
    }

    // All students fee overview — Admin only
    @GetMapping
    public String allFeesOverview(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("feeRepository", feeRepository);
        return "fee-overview";
    }
}