package com.hrplatform.hrplatform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;


    private Long positionId;

    private Long departmentId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}