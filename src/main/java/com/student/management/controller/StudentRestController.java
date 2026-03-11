package com.student.management.controller;

import com.student.management.model.Student;
import com.student.management.service.DuplicateEmailException;
import com.student.management.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CONTROLLER LAYER — REST API Controller
 * ========================================
 * Exposes RESTful JSON endpoints as required by the problem spec.
 *
 * Base URL: /api/students
 *
 * Endpoints:
 *  POST /api/students          → Create a new student (JSON body)
 *  GET  /api/students          → List all students
 *  GET  /api/students/{id}     → Get student by ID
 *  GET  /api/students/course/{course} → Get students by course
 *
 * @RestController = @Controller + @ResponseBody (returns JSON automatically)
 */
@RestController
@RequestMapping("/api/students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ─── POST /api/students ──────────────────────────────────────────────────────

    /**
     * Create a new student record.
     *
     * Request body (JSON):
     * {
     *   "name":   "Alice Smith",
     *   "email":  "alice@example.com",
     *   "course": "Computer Science"
     * }
     *
     * Responses:
     *  201 Created  → student saved successfully (returns saved student with ID)
     *  400 Bad Request → validation error or duplicate email
     */
    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody Student student) {
        try {
            Student saved = studentService.registerStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (DuplicateEmailException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error",   "Duplicate Email",
                            "message", e.getMessage(),
                            "email",   e.getEmail()
                    ));
        }
    }

    // ─── GET /api/students ───────────────────────────────────────────────────────

    /**
     * Retrieve all registered students.
     *
     * Response: 200 OK with JSON array of students
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // ─── GET /api/students/{id} ──────────────────────────────────────────────────

    /**
     * Retrieve a single student by ID.
     *
     * Response:
     *  200 OK       → student found
     *  404 Not Found → no student with that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Student not found with ID: " + id)));
    }

    // ─── GET /api/students/course/{course} ───────────────────────────────────────

    /**
     * Retrieve all students enrolled in a specific course.
     *
     * Response: 200 OK with JSON array (may be empty)
     */
    @GetMapping("/course/{course}")
    public ResponseEntity<List<Student>> getStudentsByCourse(@PathVariable String course) {
        List<Student> students = studentService.getStudentsByCourse(course);
        return ResponseEntity.ok(students);
    }
}
