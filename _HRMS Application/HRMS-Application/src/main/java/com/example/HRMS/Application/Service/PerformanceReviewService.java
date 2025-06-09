package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.DTO.PerformanceReviewResponse;
import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Entity.PerformanceReview;
import com.example.HRMS.Application.Repository.PerformanceReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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