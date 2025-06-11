package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<?> addEmployee(
            @RequestParam("employeeData") String employeeData,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Map<String, Object> response = new HashMap<>();

        try {
            Employee employee = objectMapper.readValue(employeeData, Employee.class);

            if (employeeRepository.existsByEmail(employee.getEmail())) {
                response.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            if (profilePicture != null && !profilePicture.isEmpty()) {
                String contentType = profilePicture.getContentType();
                if (isValidImageType(contentType)) {
                    employee.setProfilePicture(profilePicture.getBytes());
                } else {
                    response.put("message", "Invalid profile picture format. Only JPEG and PNG are supported.");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            Employee savedEmployee = employeeService.addEmployee(employee);
            return ResponseEntity.ok(savedEmployee);

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid employee data format.");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing profile picture.");
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/jpg")
        );
    }



//    @PostMapping
//    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
//    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
//        logger.info("POST /api/employees called");
//        Employee savedEmployee = employeeService.addEmployee(employee);
//        return ResponseEntity.ok(savedEmployee);
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        logger.info("PUT /api/employees/{} called", id);
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        logger.info("DELETE /api/employees/{} called", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    @DeleteMapping("/by-email")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    public ResponseEntity<String> deleteEmployeeByEmail(@RequestParam("email") String email) {
        logger.info("DELETE /api/employees/by-email called with email: {}", email);
        employeeService.deleteEmployeeByEmail(email);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        logger.info("GET /api/employees/{} called", id);
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("GET /api/employees called");
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
}
