package com.hrplatform.hrplatform.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Position position;

    @ManyToOne
    private Department department;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}