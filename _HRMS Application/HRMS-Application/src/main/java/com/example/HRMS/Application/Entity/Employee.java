package com.example.HRMS.Application.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String department;
    private String jobTitle;
    private String gender;

    @Enumerated(EnumType.STRING)
    private Role role;  // EMPLOYEE, HR, SENIOR_HR, MANAGER

    private LocalDate joiningDate;
    private LocalDate exitDate;


    private String status; // Active, On Notice, Resigned

    @Lob
    private byte[] profilePicture;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("employee")
    private User user;


}
