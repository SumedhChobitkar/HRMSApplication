package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.DTO.PerformanceReviewResponse;
import com.example.HRMS.Application.Entity.PerformanceReview;
import org.springframework.http.ResponseEntity;


import java.util.List;

public interface PerformanceReviewService {
    public ResponseEntity<?> createReview(PerformanceReview review);

    public ResponseEntity<?> getAllReviews();

    //public ResponseEntity<?> getReviewById(Long id);

    public ResponseEntity<?> updateReview(Long id, PerformanceReview review);

    public ResponseEntity<?> deleteReview(Long id);

    public List<PerformanceReview> getReviewsByEmployeeId(Long employeeId);

    public List<PerformanceReviewResponse> getReviewsByEmployeeEmail(String email);
}