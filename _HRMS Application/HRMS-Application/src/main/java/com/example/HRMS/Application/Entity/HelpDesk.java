package com.example.HRMS.Application.Entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HelpDesk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String subject;

    @Column(length = 2000)
    private String description;

    @Lob
    @Column(name = "attached_file", columnDefinition = "LONGBLOB")
    private byte[] attachedFile; // Store file directly

    @ElementCollection
    private List<String> ccTo;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HelpDeskStatus helpDeskStatus;

    private LocalDate date;

    private String remark;

    private String employeeName;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties("employee") // to avoid circular reference
    private Employee employee;

}
