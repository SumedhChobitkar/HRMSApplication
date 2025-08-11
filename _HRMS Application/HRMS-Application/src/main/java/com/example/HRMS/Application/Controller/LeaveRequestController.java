package com.example.HRMS.Application.Controller;
import com.example.HRMS.Application.Entity.*;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Service.LeaveRequestService;
import com.example.HRMS.Application.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController

@CrossOrigin("*")
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService service;
    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }


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
          if (toDate.isBefore(fromDate)) {
              return ResponseEntity.badRequest().body("To date must be the same or after From date.");
          }

          Employee employee = employeeRepository.findById(request.getEmployeeId())
                  .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + request.getEmployeeId()));

          request.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());

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

    @GetMapping("/leaveStatuses/{employeeId}")
    public ResponseEntity<?> getLeaveStatusesByEmployeeId(@PathVariable Long employeeId) {
        logger.info("Fetching leave statuses for employeeId: {}", employeeId);
        try {
            List<Map<String, Object>> statuses = service.getLeaveStatusesWithDatesByEmployeeId(employeeId);
            if (statuses.isEmpty()) {
                return ResponseEntity.ok("No leave statuses found for the given employee.");
            }
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            logger.error("Error fetching leave statuses: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch leave statuses.");
        }
    }

    @GetMapping("/byEmployeeName")
    public ResponseEntity<?> getLeavesByEmployeeName(@RequestParam String name) {
        logger.info("Fetching leave requests for employeeName containing: {}", name);
        List<LeaveRequest> leaves = service.getLeaveByEmployeeName(name);

        if (leaves.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No leave records found for employee name: " + name);
        }

        return ResponseEntity.ok(leaves);
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
public ResponseEntity<List<String>> getManagerEmailOptions() {
    List<Role> rolesToInclude = Arrays.asList(Role.HR, Role.SENIOR_HR, Role.MANAGER);
    List<User> eligibleUsers = userService.getUsersByRoles(rolesToInclude);

    List<String> emailOptions = eligibleUsers.stream()
            .map(user -> String.format("%s %s <%s>",
                    user.getFirstName() != null ? user.getFirstName() : "",
                    user.getLastName() != null ? user.getLastName() : "",
                    user.getEmail()))
            .collect(Collectors.toList());

    return ResponseEntity.ok(emailOptions);
}


    @GetMapping("/cc-suggestions")
    public ResponseEntity<List<String>> getCcToSuggestions(@RequestParam String query) {
        List<Role> rolesToInclude = Arrays.asList(Role.HR, Role.SENIOR_HR, Role.MANAGER);
        List<User> matchedUsers = userService.getCcSuggestions(query, rolesToInclude);

        List<String> suggestions = matchedUsers.stream()
                .map(user -> String.format("%s %s <%s>",
                        user.getFirstName() != null ? user.getFirstName() : "",
                        user.getLastName() != null ? user.getLastName() : "",
                        user.getEmail()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(suggestions);
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

    @PutMapping("/CancelLeaveById/{id}")
    public ResponseEntity<String> cancelLeave(@PathVariable("id") Long id) {
        logger.info("Cancelling leave request with ID: {}", id);
        try {
            service.cancelLeaveRequest(id);
            String message = "Leave request with ID " + id + " has been successfully cancelled.";
            logger.info(message);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            String error = "Error cancelling leave request with ID " + id + ": " + e.getMessage();
            logger.error(error);
            return ResponseEntity.status(404).body(error);
        }
    }

    @GetMapping("/download/{leaveId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long leaveId ) {
        Optional<LeaveRequest> leaveOpt = service.getLeaveRequestById(leaveId);

        if (leaveOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave request not found with ID: " + leaveId);
        }

        LeaveRequest leave = leaveOpt.get();

       if (leave.getData() == null || leave.getFileName() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No document attached for this leave request.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + leave.getFileName() + "\"")
                .body(leave.getData());
    }

}



