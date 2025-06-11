package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    @Query("SELECT l FROM LeaveRequest l WHERE l.employeeId = :employeeId AND l.status = 'APPROVED' AND " +
            "((MONTH(l.fromDate) = :month AND YEAR(l.fromDate) = :year) OR (MONTH(l.toDate) = :month AND YEAR(l.toDate) = :year))")
    List<LeaveRequest> findApprovedLeavesForMonth(Long employeeId, int year, int month);
}
