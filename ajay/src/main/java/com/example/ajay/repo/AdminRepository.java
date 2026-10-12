package com.example.ajay.repo;

import com.example.ajay.ENUM.RoleEnum;
import com.example.ajay.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(RoleEnum role);
}