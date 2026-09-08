package com.example.ajay.controller;


import com.example.ajay.entity.StudentEntity;
import com.example.ajay.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping("/create")
    public StudentEntity createStudent(@RequestBody StudentEntity student) {
        return service.saveStudent(student);
    }

    // READ ALL
    @GetMapping("/all")
    public List<StudentEntity> getAllStudents() {
        return service.getAllStudents();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public Optional<StudentEntity> getStudentById(@PathVariable int id) {
        return service.getStudentById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public StudentEntity updateStudent(
            @PathVariable int id,
            @RequestBody StudentEntity student) {

        return service.updateStudent(id, student);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable int id) {
        service.deleteStudent(id);
        return "Student deleted successfully";
    }

    // PAGINATION + SORTING
    @GetMapping("/pages")
    public Page<StudentEntity> pageAllStudents(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String direction) {

        return service.pageAllStudents(
                page,
                size,
                sortBy,
                direction
        );
    }
}