package com.example.HRMS.Application.ServiceImpl;
import com.example.HRMS.Application.DTO.PerformanceReviewResponse;
import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.PerformanceReview;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.PerformanceReviewRepository;
import com.example.HRMS.Application.Service.PerformanceReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceReviewService.class);

    @Autowired
    private PerformanceReviewRepository reviewRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    public ResponseEntity<?> createReview(PerformanceReview review) {
        if (review.getEmployee() == null || review.getEmployee().getId() == null) {
            logger.error("Employee ID is required");
            return ResponseEntity.badRequest().body("Employee ID is required");
        }

        Optional<Employee> empOpt = employeeRepo.findById(review.getEmployee().getId());

        if (!empOpt.isPresent()) {
            logger.warn("Employee not found with ID: {}", review.getEmployee().getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        //review.setEmployee(empOpt.get());
        PerformanceReview saved = reviewRepo.save(review);
        logger.info("Created performance review ID: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public ResponseEntity<?> getAllReviews() {
        List<PerformanceReview> reviews = reviewRepo.findAll();

        if (reviews.isEmpty()) {
            logger.info("No performance reviews found");
            return ResponseEntity.ok("No reviews available");
        }

        List<PerformanceReviewResponse> responseList = reviews.stream().map(this::mapToResponse).toList();
        return ResponseEntity.ok(responseList);
    }

    /*public ResponseEntity<?> getReviewById(Long id) {
        Optional<PerformanceReview> opt = reviewRepo.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(mapToResponse(opt.get()));
        } else {
            logger.warn("Review not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
    }*/
    public List<PerformanceReview> getReviewsByEmployeeId(Long employeeId) {
        if (employeeId == null) {
            logger.error("Employee ID is null");
            throw new IllegalArgumentException("Employee ID must not be null");
        }

        Optional<Employee> empOpt = employeeRepo.findById(employeeId);
        if (!empOpt.isPresent()) {
            logger.warn("Employee not found with ID: {}", employeeId);
            throw new IllegalArgumentException("Employee not found");
        }

        // List<PerformanceReview> reviews = reviewRepo.findByEmployee_id(id.getiD());
        List<PerformanceReview> reviews = reviewRepo.findByEmployee_id(employeeId);

        if (reviews.isEmpty()) {
            logger.info("No reviews found for employee ID: {}", employeeId);
        } else {
            logger.info("Found {} reviews for employee ID: {}", reviews.size(), employeeId);
        }
        return reviews;
    }

    public List<PerformanceReviewResponse> getReviewsByEmployeeEmail(String email) {
        if (email == null || email.isEmpty()) {
            logger.error("Email is null or empty");
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        List<PerformanceReview> reviews = reviewRepo.findByEmployeeEmail(email);

        if (reviews.isEmpty()) {
            logger.info("No reviews found for employee with email: {}", email);
        } else {
            logger.info("Found {} reviews for email: {}", reviews.size(), email);
        }

        return reviews.stream()
                .map(this::mapToReview)
                .collect(Collectors.toList());
    }

    private PerformanceReviewResponse mapToReview(PerformanceReview review) {
        Employee emp = review.getEmployee();
        PerformanceReviewResponse res = new PerformanceReviewResponse();

        res.setReviewId(review.getId());
        res.setTaskName(review.getTaskName());
        res.setManagerReview(review.getManagerReview());
        res.setReviewDate(review.getReviewDate());

        if (emp != null) {
            res.setEmployeeId(emp.getId());
            res.setFirstName(emp.getFirstName());
            res.setLastName(emp.getLastName());
            res.setEmail(emp.getEmail());
            res.setJobTitle(emp.getJobTitle());
        }

        return res;
    }

    public ResponseEntity<?> updateReview(Long id, PerformanceReview updatedReview) {
        Optional<PerformanceReview> existingOpt = reviewRepo.findById(id);

        if (existingOpt.isPresent()) {
            PerformanceReview existing = existingOpt.get();
            existing.setTaskName(updatedReview.getTaskName());
            existing.setManagerReview(updatedReview.getManagerReview());
            existing.setReviewDate(updatedReview.getReviewDate());

            if (updatedReview.getEmployee() != null && updatedReview.getEmployee().getId() != null) {
                Optional<Employee> empOpt = employeeRepo.findById(updatedReview.getEmployee().getId());
                if (empOpt.isPresent()) {
                    existing.setEmployee(empOpt.get());
                } else {
                    logger.warn("Update failed: Employee not found with ID {}", updatedReview.getEmployee().getId());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
                }
            }

            PerformanceReview saved = reviewRepo.save(existing);
            logger.info("Updated review ID: {}", id);
            return ResponseEntity.ok(saved);
        } else {
            logger.warn("Update failed: Review not found with ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
    }

    public ResponseEntity<?> deleteReview(Long id) {
        Optional<PerformanceReview> opt = reviewRepo.findById(id);
        if (opt.isPresent()) {
            reviewRepo.deleteById(id);
            logger.info("Deleted review ID: {}", id);
            return ResponseEntity.ok("Deleted successfully");
        } else {
            logger.warn("Delete failed: Review not found with ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
    }

    private PerformanceReviewResponse mapToResponse(PerformanceReview review) {
        Employee emp = review.getEmployee();

        PerformanceReviewResponse response = new PerformanceReviewResponse();
        response.setReviewId(review.getId());
        response.setTaskName(review.getTaskName());
        response.setManagerReview(review.getManagerReview());
        response.setReviewDate(review.getReviewDate());

        response.setEmployeeId(emp.getId());
        response.setFirstName(emp.getFirstName());
        response.setLastName(emp.getLastName());
        response.setEmail(emp.getEmail());
        response.setJobTitle(emp.getJobTitle());

        return response;
    }

}