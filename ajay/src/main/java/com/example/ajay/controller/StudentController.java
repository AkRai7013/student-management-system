package com.example.ajay.controller;

import com.example.ajay.DTO.StudentDTO;
import com.example.ajay.entity.StudentEntity;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.StudentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // CREATE STUDENT
    @PostMapping("/create")
    public ResponseEntity<ResponseGlobal<StudentDTO>> createStudent(
            @Valid @RequestBody StudentDTO student) {

        ResponseGlobal<StudentDTO> response =
                service.createStudent(student);

        if (!response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL STUDENTS
    @GetMapping("/all")
    public ResponseEntity<ResponseGlobal<List<StudentEntity>>>
    getAllStudents() {

        List<StudentEntity> students = service.getAllStudents();

        return ResponseEntity.ok(
                ResponseGlobal.onSuccess(
                        "Students fetched successfully",
                        students
                )
        );
    }

    // GET STUDENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseGlobal<StudentEntity>>
    getStudentById(@PathVariable Long id) {

        StudentEntity student = service.getStudentById(id);

        return ResponseEntity.ok(
                ResponseGlobal.onSuccess(
                        "Student fetched successfully",
                        student
                )
        );
    }

    // UPDATE STUDENT
    @PutMapping("/{id}")
    public ResponseEntity<ResponseGlobal<StudentEntity>> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentEntity student) {

        ResponseGlobal<StudentEntity> response =
                service.updateStudent(id, student);

        if (!response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    // DELETE STUDENT
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGlobal<String>> deleteStudent(
            @PathVariable Long id) {

        String message = service.deleteStudent(id);

        if (message == null
                || message.toLowerCase().contains("does not exist")
                || message.toLowerCase().contains("not found")) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseGlobal.onFailure(message));
        }

        return ResponseEntity.ok(
                ResponseGlobal.onSuccess(message, null)
        );
    }
}