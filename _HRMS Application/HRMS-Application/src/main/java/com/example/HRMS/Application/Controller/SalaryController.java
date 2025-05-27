package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setBaseSalary(@RequestParam Long empId, @RequestParam double amount) {
        return ResponseEntity.ok(salaryService.assignBaseSalary(empId, amount));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestParam Long empId, @RequestParam String month) {
        return ResponseEntity.ok(salaryService.generateSalary(empId, month));
    }

    @GetMapping("/history/{empId}")
    public ResponseEntity<?> history(@PathVariable Long empId) {
        return ResponseEntity.ok(salaryService.getSalaryHistory(empId));
    }

    @GetMapping("/slip/{empId}/{month}")
    public ResponseEntity<?> slip(@PathVariable Long empId, @PathVariable String month) {
        return salaryService.getSalarySlip(empId, month)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
