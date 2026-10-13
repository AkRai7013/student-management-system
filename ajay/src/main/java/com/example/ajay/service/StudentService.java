package com.example.ajay.service;

import com.example.ajay.DTO.StudentDTO;
import com.example.ajay.Exception.StudentNotFoundException;
import com.example.ajay.entity.Address;
import com.example.ajay.entity.Login;
import com.example.ajay.entity.StudentEntity;
import com.example.ajay.repo.LoginRepository;
import com.example.ajay.repo.StudentRepos;
import com.example.ajay.response.ResponseGlobal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepos studentRepos;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    // CREATE STUDENT
    @Transactional
    public ResponseGlobal<StudentDTO> createStudent(StudentDTO student) {

        if (student == null) {
            return ResponseGlobal.onError(
                    "Student details are required."
            );
        }

        if (student.getEmail() == null
                || student.getEmail().isBlank()) {
            return ResponseGlobal.onError(
                    "Email is required."
            );
        }

        if (student.getPassword() == null
                || student.getPassword().isBlank()) {
            return ResponseGlobal.onError(
                    "Password is required."
            );
        }

        String email = normalizeEmail(student.getEmail());

        // Check duplicate email in Student table
        if (studentRepos.findByEmail(email) != null) {
            return ResponseGlobal.onFailure(
                    "This student email is already attached to another student."
            );
        }

        // Check duplicate email in Login table
        if (loginRepository.existsByEmail(email)) {
            return ResponseGlobal.onFailure(
                    "This email is already registered for login."
            );
        }

        // Create Student
        StudentEntity newStudent = new StudentEntity();
        newStudent.setName(student.getName());
        newStudent.setAge(student.getAge());
        newStudent.setPercentage(student.getPercentage());
        newStudent.setEmail(email);
        newStudent.setStatus(true);

        // Create Address
        Address address = new Address();
        address.setCity(student.getCity());
        address.setState(student.getState());
        address.setPincode(student.getPincode());

        newStudent.setAddress(address);

        studentRepos.save(newStudent);

        // Create Login account
        Login login = new Login();
        login.setName(student.getName());
        login.setEmail(email);
        login.setPassword(
                passwordEncoder.encode(student.getPassword())
        );

        loginRepository.save(login);

        return ResponseGlobal.onSuccess(
                "Student and login details have been created successfully.",
                student
        );
    }

    // GET STUDENT BY ID
    public StudentEntity getStudentById(Long id) {

        return studentRepos.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found with id: " + id
                        )
                );
    }

    // GET ALL STUDENTS
    public List<StudentEntity> getAllStudents() {
        return studentRepos.findAll();
    }

    // UPDATE STUDENT
    @Transactional
    public ResponseGlobal<StudentEntity> updateStudent(
            Long id,
            StudentEntity student) {

        if (id == null) {
            return ResponseGlobal.onFailure(
                    "Student ID cannot be null."
            );
        }

        if (student == null) {
            return ResponseGlobal.onError(
                    "Student details are required."
            );
        }

        StudentEntity existingStudent =
                studentRepos.findById(id)
                        .orElseThrow(() ->
                                new StudentNotFoundException(
                                        "Student not found with id: " + id
                                )
                        );

        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        existingStudent.setPercentage(student.getPercentage());

        // Validate email if supplied
        if (student.getEmail() != null
                && !student.getEmail().isBlank()) {

            String email = normalizeEmail(student.getEmail());

            StudentEntity existingEmail =
                    studentRepos.findByEmail(email);

            if (existingEmail != null
                    && !existingEmail.getId().equals(id)) {
                return ResponseGlobal.onFailure(
                        "This email is already attached to another student."
                );
            }

            existingStudent.setEmail(email);
        }

        return ResponseGlobal.onSuccess(
                "Student has been updated successfully.",
                studentRepos.save(existingStudent)
        );
    }

    // DELETE STUDENT AND RELATED LOGIN
    @Transactional
    public String deleteStudent(Long id) {

        if (id == null) {
            return "Student ID cannot be null.";
        }

        StudentEntity student = studentRepos.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found with id: " + id
                        )
                );

        String email = student.getEmail();

        // Delete related Login record first
        if (email != null && !email.isBlank()) {
            loginRepository.deleteByEmail(
                    normalizeEmail(email)
            );
        }

        // Delete Student record
        studentRepos.delete(student);

        return "Student and related login account deleted successfully.";
    }

    // NORMALIZE EMAIL
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}