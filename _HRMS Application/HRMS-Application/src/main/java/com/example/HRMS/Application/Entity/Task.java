package com.example.HRMS.Application.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;
    private String assignee;

    @ElementCollection
    private List<String> checkList;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDate dueDate;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> followers;

    private String description;

    private String attachment;


    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "employee_id", nullable = false)
    // private Employee employee;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "employee_id", nullable = false)
@JsonIgnore
private Employee employee;

}
