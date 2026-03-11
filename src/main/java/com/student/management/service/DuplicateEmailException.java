package com.student.management.service;

/**
 * SERVICE LAYER — Custom Exception
 * ==================================
 * Thrown by StudentServiceImpl when a registration attempt uses
 * an email address that already exists in the database.
 *
 * This enforces the business rule: no two students may share the same email.
 */
public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        super("A student with email '" + email + "' is already registered.");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
