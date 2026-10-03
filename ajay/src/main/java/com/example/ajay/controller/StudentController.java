package com.example.ajay.controller;

import com.example.ajay.DTO.StudentDTO;
import com.example.ajay.entity.StudentEntity;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.StudentService;
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
    public ResponseGlobal<StudentDTO> createStudent(
            @RequestBody StudentDTO student) {

        return service.createStudent(student);
    }


    // GET ALL STUDENTS
    @GetMapping("/all")
    public ResponseGlobal<List<StudentEntity>> getAllStudents() {

        List<StudentEntity> students =
                service.getAllStudents();

        return ResponseGlobal.onSuccess(
                "Students fetched successfully",
                students
        );
    }


    // GET STUDENT BY ID
    @GetMapping("/{id}")
    public ResponseGlobal<StudentEntity> getStudentById(
            @PathVariable Long id) {

        StudentEntity student =
                service.getStudentById(id);

        return ResponseGlobal.onSuccess(
                "Student fetched successfully",
                student
        );
    }


    // UPDATE STUDENT
    @PutMapping("/{id}")
    public ResponseGlobal<StudentEntity> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentEntity student) {

        return service.updateStudent(id, student);
    }


    // DELETE STUDENT
    @DeleteMapping("/{id}")
    public ResponseGlobal<String> deleteStudent(
            @PathVariable Long id) {

        String message =
                service.deleteStudent(id);

        return ResponseGlobal.onSuccess(
                message,
                null
        );
    }
}