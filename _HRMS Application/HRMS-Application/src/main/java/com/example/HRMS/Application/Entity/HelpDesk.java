package com.example.HRMS.Application.Entity;


import jakarta.persistence.*;
import lombok.*;

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
}
