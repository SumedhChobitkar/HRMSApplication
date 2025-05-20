package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
}
