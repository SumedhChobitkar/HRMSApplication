package com.example.HRMS.Application.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;


    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties("user")
    private Employee employee;

    @Lob
    private byte[] profilePicture;

    private String isregistered;

    private boolean verified = false;

    @OneToOne
    @Transient
    private ForgotPasswordOtp forgotPasswordOtp;

}

