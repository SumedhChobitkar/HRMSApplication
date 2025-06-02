package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.SalaryRecord;
import com.example.HRMS.Application.Service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private  SalaryService salaryRecordService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file,
                                         @RequestParam String uploadedBy,
                                         @RequestParam String userEmail,
                                         @RequestParam String role) throws IOException {

        SalaryRecord record = salaryRecordService.upload(file, uploadedBy, userEmail);
        return ResponseEntity.ok("Uploaded successfully with ID: " + record.getId());
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        SalaryRecord record = salaryRecordService.getById(id);
//        if (role.equalsIgnoreCase("USER") && !record.getUserName().equalsIgnoreCase(userName)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
//        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(record.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + record.getFileName() + "\"")
                .body(record.getFileData());
    }

//    @GetMapping("/user/{userName}")
//    public ResponseEntity<List<SalaryRecord>> getByUser(@PathVariable String userName,
//                                                        @RequestParam String role) {
//        if (!role.equalsIgnoreCase("USER")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        return ResponseEntity.ok(salaryRecordService.getByUser(userName));
//    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SENIOR_HR')")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @RequestParam String role) {
        if (!role.equalsIgnoreCase("HR") && !role.equalsIgnoreCase("SENIOR_HR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        salaryRecordService.deleteById(id);
        return ResponseEntity.ok("Record deleted successfully");
    }


//    @GetMapping("/name/{fileName}")
//    public ResponseEntity<byte[]> getByFileName(@PathVariable String fileName) {
//        SalaryRecord record = salaryRecordService.getByFileName(fileName);
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(record.getFileType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + record.getFileName() + "\"")
//                .body(record.getFileData());
//    }

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




// old COde
//    @PostMapping("/setup")
//    public ResponseEntity<?> setBaseSalary(@RequestParam Long empId, @RequestParam double amount) {
//        return ResponseEntity.ok(salaryService.assignBaseSalary(empId, amount));
//    }
//
//    @PostMapping("/generate")
//    public ResponseEntity<?> generate(@RequestParam Long empId, @RequestParam String month) {
//        return ResponseEntity.ok(salaryService.generateSalary(empId, month));
//    }
//
//    @GetMapping("/history/{empId}")
//    public ResponseEntity<?> history(@PathVariable Long empId) {
//        return ResponseEntity.ok(salaryService.getSalaryHistory(empId));
//    }
//
//    @GetMapping("/slip/{empId}/{month}")
//    public ResponseEntity<?> slip(@PathVariable Long empId, @PathVariable String month) {
//        return salaryService.getSalarySlip(empId, month)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//}
