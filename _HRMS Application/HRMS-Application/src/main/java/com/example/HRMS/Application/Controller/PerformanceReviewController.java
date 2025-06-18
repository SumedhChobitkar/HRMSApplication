package com.example.HRMS.Application.Controller;
import com.example.HRMS.Application.DTO.PerformanceReviewResponse;
import com.example.HRMS.Application.Entity.PerformanceReview;
import com.example.HRMS.Application.Service.PerformanceReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/reviews")
public class PerformanceReviewController {

    @Autowired
    private PerformanceReviewService reviewService;

    private static final Logger logger = LoggerFactory.getLogger(PerformanceReviewController.class);


    @PostMapping("/createReview")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> createReview(@RequestBody PerformanceReview review) {
        return reviewService.createReview(review);
    }

    @GetMapping("/getAllReviews")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<?> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/getReviewByEmployeeId/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<?> getReviewsByEmployeeId(@PathVariable Long employeeId) {
        try {
            List<PerformanceReview> reviews = reviewService.getReviewsByEmployeeId(employeeId);

            if (reviews.isEmpty()) {
                logger.info("No performance reviews found for employee ID: {}", employeeId);
                return ResponseEntity.ok("No performance reviews available for this employee");
            }

            return ResponseEntity.ok(reviews);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid input or employee not found: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while fetching reviews: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/getReviewByEmail/{email}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<?> getReviewsByEmployeeEmail(@PathVariable("id") String email) {
        try {
            List<PerformanceReviewResponse> responses = reviewService.getReviewsByEmployeeEmail(email);

            if (responses.isEmpty()) {
                logger.info("No performance reviews available for email: {}", email);
                return ResponseEntity.ok("No performance reviews available for this employee");
            }

            return ResponseEntity.ok(responses);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid email input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
    @PutMapping("/updateReview/{id}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> updateReview(@PathVariable("id") Long id, @RequestBody PerformanceReview review) {
        return reviewService.updateReview(id, review);
    }

    @DeleteMapping("/DeleteById/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<?> deleteReview(@PathVariable("id") Long id) {
        
        return reviewService.deleteReview(id);
    }

}
