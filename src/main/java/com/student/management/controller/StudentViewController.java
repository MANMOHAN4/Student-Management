package com.student.management.controller;

import com.student.management.model.Student;
import com.student.management.service.DuplicateEmailException;
import com.student.management.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CONTROLLER LAYER — MVC View Controller
 * ========================================
 * Handles HTTP requests for the Thymeleaf HTML views (the UI pages).
 *
 * Endpoints:
 *  GET  /            → Renders the home page with registration form + student list
 *  POST /students    → Handles form submission, saves student, redirects back
 *
 * Uses PRG (Post-Redirect-Get) pattern to prevent duplicate form submissions
 * when the user refreshes the page after submitting.
 */
@Controller
public class StudentViewController {

    private final StudentService studentService;

    public StudentViewController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ─── GET / ──────────────────────────────────────────────────────────────────

    /**
     * Home page — shows the registration form and the current student list.
     *
     * @param model Spring MVC model passed to the Thymeleaf template
     */
    @GetMapping("/")
    public String homePage(Model model) {
        // Provide an empty Student object for the form binding (th:object)
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }

        // Fetch and display all registered students
        model.addAttribute("students", studentService.getAllStudents());

        return "index"; // → src/main/resources/templates/index.html
    }

    // ─── POST /students ─────────────────────────────────────────────────────────

    /**
     * Handle student registration form submission.
     *
     * Validation flow:
     *  1. Bean Validation (@Valid) checks @NotBlank, @Email etc.
     *  2. If validation fails → re-render form with error messages.
     *  3. Service layer checks for duplicate email.
     *  4. On success → redirect to GET / (PRG pattern).
     *
     * @param student         bound from form fields
     * @param bindingResult   holds any validation errors
     * @param redirectAttrs   used to pass flash messages across the redirect
     */
    @PostMapping("/students")
    public String registerStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult bindingResult,
            RedirectAttributes redirectAttrs,
            Model model) {

        // Step 1 — Bean Validation errors (blank fields, bad email format, etc.)
        if (bindingResult.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            return "index"; // stay on the page and show errors
        }

        try {
            // Step 2 — Business logic (duplicate email check + save)
            Student saved = studentService.registerStudent(student);
            redirectAttrs.addFlashAttribute("successMessage",
                    "Student '" + saved.getName() + "' registered successfully! (ID: " + saved.getId() + ")");

        } catch (DuplicateEmailException e) {
            // Duplicate email — send back error message via flash attribute
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttrs.addFlashAttribute("student", student); // preserve entered data
        }

        // PRG: always redirect after POST to prevent double-submission on refresh
        return "redirect:/";
    }
}
