package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployee_id(Long id);
    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.employee.email = :email")
    List<PerformanceReview> findByEmployeeEmail(@Param("email") String email);
    Optional<PerformanceReview> findByEmployeeIdAndTaskId(Long employeeId, Long taskId);


}

