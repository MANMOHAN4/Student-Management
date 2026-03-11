package com.student.management.service;

import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for StudentServiceImpl.
 * Uses Mockito to mock the StudentRepository so tests run without a real DB.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student("Alice Smith", "alice@example.com", "Computer Science");
    }

    // ── registerStudent ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("registerStudent — should save and return student when email is new")
    void registerStudent_newEmail_shouldSaveAndReturn() {
        // Arrange
        when(studentRepository.existsByEmail("alice@example.com")).thenReturn(false);

        Student savedStudent = new Student("Alice Smith", "alice@example.com", "Computer Science");
        savedStudent.setId(1L);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // Act
        Student result = studentService.registerStudent(sampleStudent);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice Smith");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");

        verify(studentRepository, times(1)).existsByEmail("alice@example.com");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("registerStudent — should throw DuplicateEmailException for existing email")
    void registerStudent_duplicateEmail_shouldThrowException() {
        // Arrange
        when(studentRepository.existsByEmail("alice@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> studentService.registerStudent(sampleStudent))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("alice@example.com");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("registerStudent — should normalise email to lowercase")
    void registerStudent_shouldNormaliseEmail() {
        // Arrange
        Student upperCaseEmail = new Student("Bob", "BOB@EXAMPLE.COM", "Physics");
        when(studentRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Student result = studentService.registerStudent(upperCaseEmail);

        // Assert — email should be lowercased by the service
        assertThat(result.getEmail()).isEqualTo("bob@example.com");
    }

    // ── getAllStudents ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllStudents — should return all students from repository")
    void getAllStudents_shouldReturnAllStudents() {
        // Arrange
        List<Student> expectedList = Arrays.asList(
                new Student("Alice", "alice@example.com", "CS"),
                new Student("Bob",   "bob@example.com",   "Math")
        );
        when(studentRepository.findAll()).thenReturn(expectedList);

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Student::getName).containsExactly("Alice", "Bob");
    }

    // ── getStudentById ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getStudentById — should return student when found")
    void getStudentById_exists_shouldReturnStudent() {
        // Arrange
        sampleStudent.setId(42L);
        when(studentRepository.findById(42L)).thenReturn(Optional.of(sampleStudent));

        // Act
        Optional<Student> result = studentService.getStudentById(42L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("getStudentById — should return empty when not found")
    void getStudentById_notExists_shouldReturnEmpty() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.getStudentById(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    // ── isEmailRegistered ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("isEmailRegistered — should return true when email exists")
    void isEmailRegistered_exists_shouldReturnTrue() {
        when(studentRepository.existsByEmail("alice@example.com")).thenReturn(true);
        assertThat(studentService.isEmailRegistered("alice@example.com")).isTrue();
    }

    @Test
    @DisplayName("isEmailRegistered — should return false when email does not exist")
    void isEmailRegistered_notExists_shouldReturnFalse() {
        when(studentRepository.existsByEmail("new@example.com")).thenReturn(false);
        assertThat(studentService.isEmailRegistered("new@example.com")).isFalse();
    }
}
