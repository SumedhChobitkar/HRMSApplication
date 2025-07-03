package com.example.HRMS.Application.Entity;

import com.example.HRMS.Application.Config.StringListConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;
    private Long employeeId; // FK to Employee, assuming no mapping for simplicity
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String applyingTo;

    // @Convert(converter = StringListConverter.class)
    // @Column( columnDefinition = "TEXT")
    // private List<String> ccTo;
//    private List<String> ccTo;
  private String ccTo;
    private String contactDetails;
    private String fileName;
    private String fileType;
    @Lob
    private byte[] data;
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Transient
    private List<String> ccToList;
    private String employeeName;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", referencedColumnName = "id", insertable = false, updatable = false)
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
//    private Employee employee;


}

