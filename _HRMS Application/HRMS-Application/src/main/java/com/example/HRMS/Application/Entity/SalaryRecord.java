package com.example.HRMS.Application.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String uploadedBy;
    private LocalDateTime uploadDate;
    private String month;
    private String userEmail; // user for whom the file is uploaded

    @Lob
    @Column(length = 10485760)
    private byte[] fileData;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    private Employee employee;
//
//    private String month; // Format: YYYY-MM
//    private double baseSalary;
//    private int presentDays;
//    private int totalWorkingDays;
//    private int paidLeaves;
//    private int unpaidLeaves;
//    private double deductions;
//    private double netSalary;
//    private LocalDate generatedDate;
}
