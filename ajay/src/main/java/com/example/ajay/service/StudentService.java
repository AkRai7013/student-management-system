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

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepos studentRepos;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;


    // CREATE STUDENT
    public ResponseGlobal<StudentDTO> createStudent(StudentDTO student) {

        // Validation
        if (student.getEmail() == null || student.getEmail().isEmpty()) {
            return ResponseGlobal.onError("Email is required.");
        }

        // Check email uniqueness
        StudentEntity existingStudent =
                studentRepos.findByEmail(student.getEmail());

        if (existingStudent != null) {
            return ResponseGlobal.onFailure(
                    "This student email is already attached to another student."
            );
        }


        // Create Student Entity
        StudentEntity newStudent = new StudentEntity();

        newStudent.setName(student.getName());
        newStudent.setAge(student.getAge());
        newStudent.setPercentage(student.getPercentage());
        newStudent.setEmail(student.getEmail());
        newStudent.setStatus(true);


        // Address
        Address address = new Address();

        address.setCity(student.getCity());
        address.setState(student.getState());
        address.setPincode(student.getPincode());

        newStudent.setAddress(address);


        // Save Student
        studentRepos.save(newStudent);


        // Create Login
        Login login = new Login();

        login.setName(student.getName());
        login.setEmail(student.getEmail());

        // Encrypt password using BCrypt
        String encryptedPassword =
                passwordEncoder.encode(student.getPassword());

        login.setPassword(encryptedPassword);

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
    public ResponseGlobal<StudentEntity> updateStudent(
            Long id,
            StudentEntity student
    ) {

        if (id == null) {
            return ResponseGlobal.onFailure(
                    "Student ID cannot be null."
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


        // Check email
        if (student.getEmail() != null) {

            StudentEntity existingEmail =
                    studentRepos.findByEmail(student.getEmail());

            if (existingEmail != null &&
                    !existingEmail.getId().equals(id)) {

                return ResponseGlobal.onFailure(
                        "This email is already attached to another student."
                );
            }

            existingStudent.setEmail(student.getEmail());
        }


        return ResponseGlobal.onSuccess(
                "Student has been updated successfully.",
                studentRepos.save(existingStudent)
        );
    }


    // DELETE STUDENT
    public String deleteStudent(Long id) {

        if (!studentRepos.existsById(id)) {

            return "Student with id " + id +
                    " does not exist.";
        }


        studentRepos.deleteById(id);

        return "Student with id " + id +
                " has been deleted successfully.";
    }
}