package com.example.HRMS.Application.Controller;
import com.example.HRMS.Application.Entity.*;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.LeaveRequestRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController

@CrossOrigin("*")
@RequestMapping("/api/leaves")
public class LeaveRequestController {
    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    @Autowired
    private LeaveRequestService service;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserService userService;

//    @PostMapping(value = "/createLeave", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<?> createLeave(@RequestPart("request") LeaveRequest request,
//                                         @RequestPart(value = "file", required = false) MultipartFile file) {
//        logger.info("Creating new leave request for employeeId: {}", request.getEmployeeId());
//
//        if (request.getEmployeeId() == null) {
//            return ResponseEntity.badRequest().body("Employee ID is required.");
//        }
//
//        try {
//            LocalDate fromDate = request.getFromDate();
//            LocalDate toDate = request.getToDate();
//            LocalDate today = LocalDate.now();
//
//            if (fromDate.isBefore(today.minusMonths(1))) {
//                return ResponseEntity.badRequest().body("You can only apply for leave going back 1 month.");
//            }
//
//            if (fromDate.isAfter(today.plusMonths(6))) {
//                return ResponseEntity.badRequest().body("You can't apply for leave more than 6 months in advance.");
//            }
//
//            if (toDate.isBefore(fromDate)) {
//                return ResponseEntity.badRequest().body("To date must be the same or after From date.");
//            }
//
//            // Prevent unpaid leave
//            if (request.getLeaveType() == LeaveType.UNPAID) {
//                return ResponseEntity.badRequest().body("Unpaid leave is not allowed.");
//            }
//
//            // Fetch employee
//            Employee employee = employeeRepository.findById(request.getEmployeeId())
//                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + request.getEmployeeId()));
//
//            request.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
//
//            // Validate leave limits
//            ResponseEntity<?> limitCheck = validateLeaveLimits(request, file);
//            if (limitCheck != null) {
//                return limitCheck; // return error message if limit exceeded
//            }
//
//            LeaveRequest created = service.createLeaveRequest(request, file);
//            return ResponseEntity.ok(created);
//
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            logger.error("Error creating leave request: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Server error while creating leave request.");
//        }
//    }
    @PostMapping(value = "/createLeave", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
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

            // Fetch employee
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + request.getEmployeeId()));

            request.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());

            // Validate leave limits
            ResponseEntity<?> limitCheck = validateLeaveLimits(request, file);
            if (limitCheck != null) {
                return limitCheck; // return error message if limit exceeded
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

//    private ResponseEntity<?> validateLeaveLimits(LeaveRequest request, MultipartFile file) {
//        List<LeaveRequest> approvedLeaves = leaveRequestRepository
//                .findByEmployeeIdAndStatus(request.getEmployeeId(), LeaveStatus.APPROVED);
//
//        long usedDays = 0;
//        for (LeaveRequest lr : approvedLeaves) {
//            if (lr.getLeaveType() == request.getLeaveType()) {
//                usedDays += ChronoUnit.DAYS.between(lr.getFromDate(), lr.getToDate()) + 1;
//            }
//        }
//
//        long requestedDays = ChronoUnit.DAYS.between(request.getFromDate(), request.getToDate()) + 1;
//        long allowedDays;
//
//        if (request.getLeaveType() == LeaveType.SICK) {
//            allowedDays = 3;
//        } else if (request.getLeaveType() == LeaveType.CASUAL) {
//            allowedDays = 3;
//        } else if (request.getLeaveType() == LeaveType.PAID) {
//            allowedDays = 3;
//        } else if (request.getLeaveType() == LeaveType.MATERNITY) {
//            allowedDays = 180;
//            if (file == null || file.isEmpty()) {
//                return ResponseEntity.badRequest().body("Maternity leave requires a supporting document.");
//            }
//        } else {
//            return ResponseEntity.badRequest().body("Invalid leave type.");
//        }
//
//        long remainingDays = allowedDays - usedDays;
//        if (requestedDays > remainingDays) {
//            return ResponseEntity.badRequest().body("You have only " + remainingDays + " "
//                    + request.getLeaveType() + " leave days remaining. You requested " + requestedDays + " days.");
//        }
//
//        return null;
//    }
    private ResponseEntity<?> validateLeaveLimits(LeaveRequest request, MultipartFile file) {
        //validation for unpaid leave (unlimited days)
        if (request.getLeaveType() == LeaveType.UNPAID) {
            return null;
        }

        List<LeaveRequest> approvedLeaves = leaveRequestRepository
                .findByEmployeeIdAndStatus(request.getEmployeeId(), LeaveStatus.APPROVED);

        long usedDays = 0;
        for (LeaveRequest lr : approvedLeaves) {
            if (lr.getLeaveType() == request.getLeaveType()) {
                usedDays += ChronoUnit.DAYS.between(lr.getFromDate(), lr.getToDate()) + 1;
            }
        }

        long requestedDays = ChronoUnit.DAYS.between(request.getFromDate(), request.getToDate()) + 1;
        long allowedDays;

        if (request.getLeaveType() == LeaveType.SICK) {
            allowedDays = 3;
        } else if (request.getLeaveType() == LeaveType.CASUAL) {
            allowedDays = 3;
        } else if (request.getLeaveType() == LeaveType.PAID) {
            allowedDays = 3;
        } else if (request.getLeaveType() == LeaveType.MATERNITY) {
            allowedDays = 180;
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Maternity leave requires a supporting document.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid leave type.");
        }

        long remainingDays = allowedDays - usedDays;
        if (requestedDays > remainingDays) {
            return ResponseEntity.badRequest().body("You have only " + remainingDays + " "
                    + request.getLeaveType() + " leave days remaining. You requested " + requestedDays + " days.");
        }

        return null;
    }

@PutMapping("/updateStatus/{leaveId}")
public ResponseEntity<?> updateLeaveStatus(@PathVariable Long leaveId,
                                           @RequestParam(required = false) LeaveStatus status) {
    logger.info("Updating status for leaveId: {} to {}", leaveId, status);

    try {
        LeaveRequest updatedLeave = service.updateLeaveStatus(leaveId, status);
        return ResponseEntity.ok(updatedLeave);
    } catch (IllegalArgumentException e) {
        logger.warn("Validation failed while updating status for leaveId {}: {}", leaveId, e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (RuntimeException e) {
        logger.error("Leave request not found for leaveId {}: {}", leaveId, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        logger.error("Error updating leave status for leaveId {}: {}", leaveId, e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Server error while updating leave status.");
    }
}


    @GetMapping("/leaveBalance/{employeeId}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable Long employeeId) {
        logger.info("Fetching leave balance for employeeId: {}", employeeId);

        try {
            Map<String, String> leaveBalance = service.getLeaveBalance(employeeId);
            return ResponseEntity.ok(leaveBalance);
        } catch (Exception e) {
            logger.error("Error fetching leave balance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error while fetching leave balance.");
        }
    }


  /*@PostMapping(value = "/createLeave", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
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
  }*/
  /*@PostMapping(value = "/createLeave", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
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
    @PutMapping("/updateStatusById/{LeaveId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestParam LeaveStatus status) {
        logger.info("Updating status of leave request ID {} to {}", id, status);
        try {
            LeaveRequest updated = service.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Failed to update status for leave ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Failed to update leave status.");
        }
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
    }*/


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

//    @PutMapping("/updateStatusById/{id}/status")
//    public ResponseEntity<LeaveRequest> updateStatus(@PathVariable("id") Long id, @RequestParam LeaveStatus status) {
//        logger.info("Updating status of leave request ID {} to {}", id, status);
//        try {
//            LeaveRequest updated = service.updateStatus(id, status);
//            return ResponseEntity.ok(updated);
//        } catch (Exception e) {
//            logger.error("Failed to update status for leave ID {}: {}", id, e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }


    @GetMapping("/applyingTo")
    public ResponseEntity<List<String>> getManagerEmailOptions() {
        List<Role> rolesToInclude = Arrays.asList(Role.MANAGER);
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


    @PutMapping(value = "/updateLeave/{leaveId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateLeave(@PathVariable Long leaveId,
                                         @RequestPart("request") LeaveRequest updatedRequest,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        logger.info("Updating leave request ID: {}", leaveId);

        try {
            LeaveRequest updated = service.updateLeave(leaveId, updatedRequest, file);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating leave request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error while updating leave request.");
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
    public ResponseEntity<Map<String, String>> cancelLeave(@PathVariable("id") Long id) {
        logger.info("Cancelling leave request with ID: {}", id);
        try {
            service.cancelLeaveRequest(id);
            return ResponseEntity.ok(Map.of("message", "Leave request with ID " + id + " has been successfully cancelled."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
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
    @GetMapping("/getRemark/{leaveId}")
    public ResponseEntity<?> getRemarkByLeaveId(@PathVariable Long leaveId) {
        logger.info("Fetching remark for leaveId: {}", leaveId);
        try {
            String remark = service.getRemarkByLeaveId(leaveId);
            return ResponseEntity.ok(remark);
        } catch (RuntimeException e) {
            logger.warn("Leave request not found with ID: {}", leaveId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error while fetching remark: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch remark.");
        }
    }

    @PostMapping("/addRemark/{leaveId}")
    public ResponseEntity<?> addRemark(@PathVariable Long leaveId,
                                       @RequestParam String remark) {
        try {
            LeaveRequest updatedLeave = service.addRemark(leaveId, remark);
            return ResponseEntity.ok(updatedLeave);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error while adding remark: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add remark.");
        }
    }


}



