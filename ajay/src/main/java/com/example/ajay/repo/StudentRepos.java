package com.example.ajay.repo;

import com.example.ajay.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepos extends JpaRepository<StudentEntity, Long> {

    List<StudentEntity> findByName(String name);

    StudentEntity findByEmail(String email);

    List<StudentEntity> findByAgeGreaterThan(int age);
}