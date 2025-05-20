package com.example.HRMS.Application.Controller;
import com.example.HRMS.Application.Entity.LeaveRequest;
import com.example.HRMS.Application.Service.LeaveRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService service;

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }

    @PostMapping("/createLeave")
    public ResponseEntity<LeaveRequest> createLeave(@RequestBody LeaveRequest request) {
        logger.info("Creating new leave request: {}", request);
        LeaveRequest created = service.createLeaveRequest(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/getAllLeaves")
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        logger.info("Fetching all leave requests");
        List<LeaveRequest> leaves = service.getAllLeaveRequests();
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/getLeaveById/{id}")
    public ResponseEntity<LeaveRequest> getLeaveById(@PathVariable Long id) {
        logger.info("Fetching leave request by ID: {}", id);
        return service.getLeaveRequestById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Leave request not found for ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/updateStatusById/{id}/status")
    public ResponseEntity<LeaveRequest> updateStatus(@PathVariable Long id, @RequestParam LeaveRequest.Status status) {
        logger.info("Updating status of leave request ID {} to {}", id, status);
        try {
            LeaveRequest updated = service.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Failed to update status for leave ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/DeleteLeaveById/{id}")
    public ResponseEntity<String> deleteLeave(@PathVariable Long id) {
        logger.info("Deleting leave request with ID: {}", id);
        try {
            service.deleteLeaveRequest(id);
            String message = "Leave request with ID " + id + " has been successfully deleted.";
            logger.info(message);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String error = "Error deleting leave request with ID " + id + ": " + e.getMessage();
            logger.error(error);
            return ResponseEntity.status(404).body(error);
        }
    }

}



