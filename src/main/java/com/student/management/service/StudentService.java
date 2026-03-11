package com.student.management.service;

import com.student.management.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE LAYER — Interface
 * ==========================
 * Defines the business operations available for Student management.
 * Using an interface allows easy swapping of implementations (e.g. for testing).
 */
public interface StudentService {

    /**
     * Register a new student.
     * Throws DuplicateEmailException if the email is already registered.
     *
     * @param student the student to register
     * @return the saved student (with auto-generated ID)
     */
    Student registerStudent(Student student);

    /**
     * Retrieve all students from the database.
     *
     * @return list of all students
     */
    List<Student> getAllStudents();

    /**
     * Find a single student by their unique ID.
     *
     * @param id the student's primary key
     * @return Optional containing the student, or empty if not found
     */
    Optional<Student> getStudentById(Long id);

    /**
     * Find all students enrolled in a specific course.
     *
     * @param course the course name
     * @return list of matching students
     */
    List<Student> getStudentsByCourse(String course);

    /**
     * Check if an email address is already registered.
     *
     * @param email the email to check
     * @return true if the email is taken, false otherwise
     */
    boolean isEmailRegistered(String email);
}
