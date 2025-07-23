package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.Attendance;
import com.example.HRMS.Application.Entity.RegularizationAndPermission;
import com.example.HRMS.Application.Service.AttendanceService;
import com.example.HRMS.Application.Service.RegularizationAndPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/regularization-and-permission")
public class RegularizationAndPermissionController {

    @Autowired
    private RegularizationAndPermissionService service;

    @PostMapping("/request-regularization/{employeeId}")
    public ResponseEntity<Map<String, Object>> requestRegularization(
            @PathVariable Long employeeId,
            @RequestBody RegularizationAndPermission request) {

        RegularizationAndPermission result = service.requestRegularization(employeeId, request);
        return ResponseEntity.ok(Map.of(
                "message", "Regularization requested",
                "status", result.getApprovalStatus(),
                "reason", result.getReason()
        ));
    }

    @PostMapping("/request-permission/{employeeId}")
    public ResponseEntity<Map<String, Object>> requestPermission(
            @PathVariable Long employeeId,
            @RequestBody RegularizationAndPermission request) {

        RegularizationAndPermission result = service.requestPermission(employeeId, request);
        return ResponseEntity.ok(Map.of(
                "message", "Permission requested",
                "status", result.getApprovalStatus(),
                "reason", result.getReason()
        ));
    }

    @PutMapping("/approve/{requestId}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<RegularizationAndPermission> approveRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(service.approveRequest(requestId));
    }

    @PutMapping("/reject/{requestId}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<RegularizationAndPermission> rejectRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(service.rejectRequest(requestId));
    }

    @GetMapping("/pending-requests")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<List<RegularizationAndPermission>> getAllPendingRequests() {
        return ResponseEntity.ok(service.getAllPendingRequests());
    }

    // Get all permission requests by employee ID
    @GetMapping("/permissions/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<List<RegularizationAndPermission>> getPermissionsByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getPermissionsByEmployeeId(employeeId));
    }

    // Get all regularization requests by employee ID
    @GetMapping("/regularizations/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<List<RegularizationAndPermission>> getRegularizationsByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getRegularizationsByEmployeeId(employeeId));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<String> deleteRequestById(@PathVariable Long id) {
        service.deleteRequestById(id);
        return ResponseEntity.ok("Request deleted successfully with ID: " + id);

    }
}
