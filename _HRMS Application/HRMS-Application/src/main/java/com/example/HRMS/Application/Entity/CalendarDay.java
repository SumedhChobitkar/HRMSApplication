package com.example.HRMS.Application.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "calendar_days")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Boolean isWorkingDay;

    private String remarks;


}
