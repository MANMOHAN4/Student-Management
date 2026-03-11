package com.student.management.service;

import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE LAYER — Implementation
 * ================================
 * Contains all business logic for the Student Management System.
 *
 * Responsibilities:
 *  - Prevent duplicate email registrations
 *  - Coordinate with StudentRepository for data persistence
 *  - Keep Controller lean by centralising all rules here
 *
 * @Transactional ensures that write operations are atomic;
 * if anything fails mid-way, the entire operation is rolled back.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    /**
     * Constructor injection — preferred over field injection.
     * Makes dependencies explicit and testable.
     */
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ─── Create ─────────────────────────────────────────────────────────────────

    /**
     * Register a new student.
     *
     * Business Rules:
     *  1. Email must not already exist in the database.
     *  2. All field validation is handled by Bean Validation (@NotBlank etc.)
     *     before this method is even called.
     *
     * @throws DuplicateEmailException if the email is already registered
     */
    @Override
    public Student registerStudent(Student student) {
        // Business Rule: Prevent duplicate email registration
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(student.getEmail());
        }

        // Trim whitespace from text fields before saving
        student.setName(student.getName().trim());
        student.setEmail(student.getEmail().trim().toLowerCase());
        student.setCourse(student.getCourse().trim());

        return studentRepository.save(student);
    }

    // ─── Read ────────────────────────────────────────────────────────────────────

    /**
     * Retrieve all students, ordered by ID (insertion order).
     */
    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Find a single student by their primary key.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    /**
     * Find all students enrolled in a specific course.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Student> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course);
    }

    /**
     * Check whether the given email is already registered.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailRegistered(String email) {
        return studentRepository.existsByEmail(email);
    }
}
