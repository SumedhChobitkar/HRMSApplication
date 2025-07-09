package com.example.HRMS.Application.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(name = "performance_reviews")
@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReview {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String taskName;
        private String managerReview;
        private LocalDate reviewDate;

        @ManyToOne
        @JoinColumn(name = "employee_id", nullable = false)
        private Employee employee;

        @ManyToOne
        @JoinColumn(name = "task_id", nullable = false)
        private Task task;
    }


