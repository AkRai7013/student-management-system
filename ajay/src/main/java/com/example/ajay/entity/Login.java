package com.example.ajay.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "login")
@Data
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;
}