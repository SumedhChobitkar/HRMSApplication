package com.example.HRMS.Application.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Table(name = "regularization_and_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegularizationAndPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequestType requestType; // REGULARIZATION or PERMISSION

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private String reason;
    private LocalDate date;
    private LocalTime clockIn;
    private LocalTime clockOut;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
