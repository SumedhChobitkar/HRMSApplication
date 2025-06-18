package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.SalaryRecord;
import com.example.HRMS.Application.Service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private  SalaryService salaryRecordService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam ("uploadedBy") String uploadedBy,
                                         @RequestParam("userEmail") String userEmail,
                                         @RequestParam ("role")String role) throws IOException {
        try {
            SalaryRecord record = salaryRecordService.upload(file, uploadedBy, userEmail);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Uploaded successfully");
            response.put("id", record.getId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/email/{userEmail}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER','USER')")
    public ResponseEntity<List<SalaryRecord>> getAllByUserEmail(@PathVariable ("userEmail") String userEmail) {
        return ResponseEntity.ok(salaryRecordService.getAllByUserEmail(userEmail));
    }


    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER','USER') ")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable ("id") Long id) {
        SalaryRecord record = salaryRecordService.getById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(record.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + record.getFileName() + "\"")
                .body(record.getFileData());
    }


    @GetMapping("/Email/month")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER','USER') ")
    public ResponseEntity<SalaryRecord> getSalaryByEmailAndMonth(
            @RequestParam String email,
            @RequestParam String month) {
        SalaryRecord record = salaryRecordService.getByEmailAndMonth(email, month).orElse(null);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    public ResponseEntity<String> delete(@PathVariable("id") Long id
                                        ) {
        salaryRecordService.deleteById(id);
        return ResponseEntity.ok("Record deleted successfully");
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<SalaryRecord>> getByMonth(@PathVariable String month) {
        return ResponseEntity.ok(salaryRecordService.getByMonth(month));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR', 'MANAGER')")
    public ResponseEntity<List<SalaryRecord>> getAll() {
        return ResponseEntity.ok(salaryRecordService.getAll());
    }
    }