package com.example.HRMS.Application.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BaseSalary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Employee employee;

    private double amount;
}
