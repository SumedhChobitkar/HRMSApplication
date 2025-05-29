package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        logger.info("POST /api/employees called");
        Employee savedEmployee = employeeService.addEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

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
