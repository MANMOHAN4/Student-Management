package com.student.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests — loads the full Spring context with H2 database.
 * Tests the REST API endpoints end-to-end.
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    // ── POST /api/students ───────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/students — should create student and return 201")
    void createStudent_validInput_returns201() throws Exception {
        Student student = new Student("Alice Smith", "alice@test.com", "Computer Science");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id",     notNullValue()))
               .andExpect(jsonPath("$.name",   is("Alice Smith")))
               .andExpect(jsonPath("$.email",  is("alice@test.com")))
               .andExpect(jsonPath("$.course", is("Computer Science")));
    }

    @Test
    @DisplayName("POST /api/students — duplicate email returns 400")
    void createStudent_duplicateEmail_returns400() throws Exception {
        Student student = new Student("Alice Smith", "alice@test.com", "Computer Science");

        // First registration — should succeed
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
               .andExpect(status().isCreated());

        // Second registration with same email — should fail
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error", is("Duplicate Email")));
    }

    @Test
    @DisplayName("POST /api/students — blank name returns 400")
    void createStudent_blankName_returns400() throws Exception {
        Student student = new Student("", "alice@test.com", "CS");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
               .andExpect(status().isBadRequest());
    }

    // ── GET /api/students ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/students — returns all students")
    void getAllStudents_returnsAll() throws Exception {
        studentRepository.save(new Student("Alice", "alice@test.com", "CS"));
        studentRepository.save(new Student("Bob",   "bob@test.com",   "Math"));

        mockMvc.perform(get("/api/students"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].name", is("Alice")))
               .andExpect(jsonPath("$[1].name", is("Bob")));
    }

    @Test
    @DisplayName("GET /api/students — returns empty array when no students")
    void getAllStudents_empty_returnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/students"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── GET /api/students/{id} ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/students/{id} — returns student when found")
    void getStudentById_found_returns200() throws Exception {
        Student saved = studentRepository.save(new Student("Alice", "alice@test.com", "CS"));

        mockMvc.perform(get("/api/students/" + saved.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("Alice")));
    }

    @Test
    @DisplayName("GET /api/students/{id} — returns 404 when not found")
    void getStudentById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/students/9999"))
               .andExpect(status().isNotFound());
    }

    // ── GET / (home page) ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET / — home page loads with 200 OK")
    void homePage_loads_returns200() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attributeExists("student", "students"));
    }
}
