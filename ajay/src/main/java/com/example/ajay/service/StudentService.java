package com.example.ajay.service;



import com.example.ajay.entity.StudentEntity;
import com.example.ajay.repo.StudentRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class StudentService {

    private final StudentRepo repository;

    public StudentService(StudentRepo repository) {
        this.repository = repository;
    }

    // CREATE
    public StudentEntity saveStudent(StudentEntity student) {
        return repository.save(student);
    }

    // READ ALL
    public List<StudentEntity> getAllStudents() {
        return repository.findAll();
    }

    // READ BY ID
    public Optional<StudentEntity> getStudentById(int id) {
        return repository.findById(id);
    }

    // UPDATE
    public StudentEntity updateStudent(int id, StudentEntity student) {

        StudentEntity existingStudent = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        existingStudent.setName(student.getName());
        existingStudent.setPercentage(student.getPercentage());
        existingStudent.setAddress(student.getAddress());

        return repository.save(existingStudent);
    }

    // DELETE
    public void deleteStudent(int id) {
        repository.deleteById(id);
    }

    public Page<StudentEntity> pageAllStudents(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort;

        if (direction.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return repository.findAll(pageRequest);}
}