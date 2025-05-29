package com.example.HRMS.Application.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class SalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    private String month; // Format: YYYY-MM
    private double baseSalary;
    private int presentDays;
    private int totalWorkingDays;
    private int paidLeaves;
    private int unpaidLeaves;
    private double deductions;
    private double netSalary;
    private LocalDate generatedDate;
}
