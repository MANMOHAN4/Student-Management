package com.student.management.repository;

import com.student.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY LAYER
 * ================
 * Spring Data JPA repository for the Student entity.
 *
 * Extends JpaRepository which automatically provides:
 *  - save(Student)         → INSERT / UPDATE
 *  - findById(Long)        → SELECT by primary key
 *  - findAll()             → SELECT all students
 *  - deleteById(Long)      → DELETE by primary key
 *  - count()               → COUNT(*)
 *  + many more
 *
 * Custom query methods are derived automatically from method names
 * by Spring Data JPA (no SQL needed).
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find a student by their email address.
     * Used by the service layer to detect duplicate registrations.
     *
     * Derived query → SELECT * FROM students WHERE email = ?
     */
    Optional<Student> findByEmail(String email);

    /**
     * Find all students enrolled in a specific course.
     *
     * Derived query → SELECT * FROM students WHERE course = ?
     */
    List<Student> findByCourse(String course);

    /**
     * Check whether a student with the given email already exists.
     *
     * Derived query → SELECT COUNT(*) > 0 FROM students WHERE email = ?
     */
    boolean existsByEmail(String email);
}
