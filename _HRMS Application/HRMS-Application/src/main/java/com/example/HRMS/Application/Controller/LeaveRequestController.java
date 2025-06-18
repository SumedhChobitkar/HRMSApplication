package com.example.HRMS.Application.Controller;
import com.example.HRMS.Application.Entity.LeaveRequest;
import com.example.HRMS.Application.Entity.LeaveStatus;
import com.example.HRMS.Application.Service.LeaveRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController

@CrossOrigin("*")
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService service;

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }

  /*  @PostMapping(value = "/createLeave", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createLeave(@RequestPart(value = "request") LeaveRequest request,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        logger.info("Creating new leave request: {}", request);
        logger.info("Creating new leave request for employeeId: {}", request.getEmployeeId());

        if (request.getEmployeeId() == null) {
            return ResponseEntity.badRequest().body("Employee ID is required.");
        }

        if (file != null) {
            logger.info("File attached: {}", file.getOriginalFilename());
        }

        try {
            // Validate leave dates: allow backdated leave up to 1 month ago
            LocalDate fromDate = request.getFromDate();
            LocalDate toDate = request.getToDate();
            LocalDate today = LocalDate.now();

            if (fromDate.isBefore(today.minusMonths(1))) {
                return ResponseEntity.badRequest().body("You can only apply for leave going back 1 month.");
            }

            if (fromDate.isAfter(today.plusMonths(6))) {
                return ResponseEntity.badRequest().body("You can't apply for leave more than 6 months in advance.");
            }

            // Save the leave request (with or without attachment)
            LeaveRequest created = service.createLeaveRequest(request, file);
            return ResponseEntity.ok(created);

        } catch (Exception e) {
            logger.error("Error creating leave request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error while creating leave request.");
        }
    }*/
  @PostMapping(value = "/createLeave", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> createLeave(@RequestPart("request") LeaveRequest request,
                                       @RequestPart(value = "file", required = false) MultipartFile file) {
      logger.info("Creating new leave request for employeeId: {}", request.getEmployeeId());

      if (request.getEmployeeId() == null) {
          return ResponseEntity.badRequest().body("Employee ID is required.");
      }

      try {
          LocalDate fromDate = request.getFromDate();
          LocalDate toDate = request.getToDate();
          LocalDate today = LocalDate.now();

          if (fromDate.isBefore(today.minusMonths(1))) {
              return ResponseEntity.badRequest().body("You can only apply for leave going back 1 month.");
          }

          if (fromDate.isAfter(today.plusMonths(6))) {
              return ResponseEntity.badRequest().body("You can't apply for leave more than 6 months in advance.");
          }

          LeaveRequest created = service.createLeaveRequest(request, file);
          return ResponseEntity.ok(created);

      } catch (IllegalArgumentException e) {
          return ResponseEntity.badRequest().body(e.getMessage());
      } catch (Exception e) {
          logger.error("Error creating leave request: {}", e.getMessage(), e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Server error while creating leave request.");
      }
  }



    @GetMapping("/getAllLeaves")
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        logger.info("Fetching all leave requests");
        List<LeaveRequest> leaves = service.getAllLeaveRequests();
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/getLeaveById/{id}")
    public ResponseEntity<LeaveRequest> getLeaveById(@PathVariable("id") Long id) {
        logger.info("Fetching leave request by ID: {}", id);
        return service.getLeaveRequestById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Leave request not found for ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/updateStatusById/{id}/status")
    public ResponseEntity<LeaveRequest> updateStatus(@PathVariable("id") Long id, @RequestParam LeaveStatus status) {
        logger.info("Updating status of leave request ID {} to {}", id, status);
        try {
            LeaveRequest updated = service.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Failed to update status for leave ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/applyingTo")
    public ResponseEntity<?> getApplyingToEmail() {
        logger.info("Fetching HR email for applyingTo field");

        String email = service.getApplyingToEmail();
        if (email == null || email.isEmpty()) {
            logger.error("HR email not found");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("HR email not configured");
        }
        return ResponseEntity.ok(email);
    }

    // 2. Get list of all employee emails for ccTo
    @GetMapping("/ccToEmployees")
    public ResponseEntity<?> getAllEmployeeEmails() {
        logger.info("Fetching list of employee emails for ccTo");

        List<String> emails =service.getAllEmployeeEmails();
        if (emails.isEmpty()) {
            logger.warn("No employees found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No employee emails found");
        }

        return ResponseEntity.ok(emails);
    }
    /*@GetMapping("/leaveBalance/{employeeId}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable Long employeeId) {
        logger.info("Fetching leave balance for employeeId: {}", employeeId);

        try {
            Map<String, Object> leaveBalance = service.getLeaveBalance(employeeId);

            if (leaveBalance.isEmpty()) {
                logger.warn("No approved leave records found for employeeId: {}", employeeId);
                return ResponseEntity.ok("No leave data found for the employee.");
            }

            return ResponseEntity.ok(leaveBalance);

        } catch (Exception e) {
            logger.error("Error occurred while fetching leave balance for employeeId: {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch leave balance.");
        }
    }*/
    @GetMapping("/leaveBalance/{employeeId}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable Long employeeId) {
        logger.info("Fetching leave balance for employeeId: {}", employeeId);

        try {
            Map<String, Object> leaveBalance = service.getLeaveBalance(employeeId);
            if (leaveBalance.isEmpty()) {
                logger.warn("No approved leave records found for employeeId: {}", employeeId);
                return ResponseEntity.ok("No leave data found for the employee.");
            }
            return ResponseEntity.ok(leaveBalance);
        } catch (Exception e) {
            logger.error("Error occurred while fetching leave balance for employeeId: {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch leave balance.");
        }
    }



    @DeleteMapping("/DeleteLeaveById/{id}")
    public ResponseEntity<String> deleteLeave(@PathVariable("id") Long id) {
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



