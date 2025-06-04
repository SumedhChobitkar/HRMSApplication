package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.SalaryRecord;
import com.example.HRMS.Application.Exception.ResourceNotFoundException;
import com.example.HRMS.Application.Repository.BaseSalaryRepository;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.SalaryRecordRepository;
import com.example.HRMS.Application.Service.SalaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final BaseSalaryRepository baseSalaryRepo;
    private final SalaryRecordRepository salaryRepo;
    private final EmployeeRepository employeeRepository;

    public SalaryServiceImpl(BaseSalaryRepository baseSalaryRepo, SalaryRecordRepository salaryRepo, EmployeeRepository employeeRepo) {
        this.baseSalaryRepo = baseSalaryRepo;
        this.salaryRepo = salaryRepo;
        this.employeeRepository = employeeRepo;
    }


    @Override
    public SalaryRecord upload(MultipartFile file, String uploadedBy, String userEmail) throws IOException {
        // Check if employee exists by email
        Employee employee = employeeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        String month = LocalDateTime.now().getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "-" + LocalDateTime.now().getYear();

        Optional<SalaryRecord> existingRecord = salaryRepo.findByUserEmailAndMonth(userEmail, month);
        if (existingRecord.isPresent()) {
            throw new IllegalArgumentException("Salary record for this employee already exists for the month: " + month);
        }


        SalaryRecord record = SalaryRecord.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileData(file.getBytes())
                .userEmail(userEmail)
                .uploadedBy(uploadedBy)
                .uploadDate(LocalDateTime.now())
                .month(month)
                .employee(employee)
                .build();

        return salaryRepo.save(record);
    }

//    @Override
//    public Optional<SalaryRecord> getByUser(String userEmail) {
//        Optional<SalaryRecord> user = salaryRepo.findByUserEmail(userEmail);
//        if (user.isEmpty()) {
//            throw new ResourceNotFoundException("User with email " + userEmail + " not found.");
//        }
//
//        return user;
//    }

    @Override
    public List<SalaryRecord> getAllByUserEmail(String userEmail) {
        Employee employee = employeeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        List<SalaryRecord> records = salaryRepo.findByEmployee(employee);
        if (records.isEmpty()) {
            throw new ResourceNotFoundException("No salary records found for email: " + userEmail);
        }

        return records;
    }



    @Override
    public SalaryRecord getById(Long id) {
        return salaryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with ID " + id));
    }




    @Override
    public Optional<SalaryRecord> getByEmailAndMonth(String email, String month) {
        Optional<SalaryRecord> record = salaryRepo.findByUserEmailAndMonth(email, month);
        if (record.isEmpty()) {
            throw new ResourceNotFoundException("No record found for user: " + email + " in month: " + month);
        }
        return record;

    }




//    @Override
//    public SalaryRecord getByFileName(String fileName) {
//        return salaryRepo.findByFileName(fileName)
//                .orElseThrow(() -> new ResourceNotFoundException("File not found: " + fileName));
//    }

    @Override
    public List<SalaryRecord> getByMonth(String month) {
        return salaryRepo.findAllByMonth(month);
    }

    @Override
    public void deleteById(Long id) {
        if (!salaryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Salary record not found with ID " + id);
        }
        salaryRepo.deleteById(id);
    }





    @Override
    public List<SalaryRecord> getAll() {
        return salaryRepo.findAll();
    }

//    @Override
//    public BaseSalary assignBaseSalary(Long empId, double amount) {
//        Employee emp = employeeRepo.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        BaseSalary base = baseSalaryRepo.findByEmployeeId(empId).orElse(new BaseSalary());
//        base.setEmployee(emp);
//        base.setAmount(amount);
//        return baseSalaryRepo.save(base);
//    }
//
//    @Override
//    public SalaryRecord generateSalary(Long empId, String month) {
//        Employee emp = employeeRepo.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found"));
//        BaseSalary baseSalary = baseSalaryRepo.findByEmployeeId(empId).orElseThrow(() -> new RuntimeException("Base salary not set"));
//
//        int totalDays = YearMonth.parse(month).lengthOfMonth();
//        int presentDays = (int) (Math.random() * totalDays); // Replace with attendance repo count
//        int paidLeaves = 2; // replace with real leave logic
//        int unpaidLeaves = totalDays - presentDays - paidLeaves;
//        double deduction = (unpaidLeaves * baseSalary.getAmount()) / totalDays;
//
//        SalaryRecord record = new SalaryRecord();
//        record.setEmployee(emp);
//        record.setMonth(month);
//        record.setBaseSalary(baseSalary.getAmount());
//        record.setPresentDays(presentDays);
//        record.setTotalWorkingDays(totalDays);
//        record.setPaidLeaves(paidLeaves);
//        record.setUnpaidLeaves(unpaidLeaves);
//        record.setDeductions(deduction);
//        record.setNetSalary(baseSalary.getAmount() - deduction);
//        record.setGeneratedDate(LocalDate.now());
//
//        return salaryRepo.save(record);
//    }
//
//    @Override
//    public List<SalaryRecord> getSalaryHistory(Long empId) {
//        return salaryRepo.findByEmployeeId(empId);
//    }
//
//    @Override
//    public Optional<SalaryRecord> getSalarySlip(Long empId, String month) {
//        return salaryRepo.findByEmployeeIdAndMonth(empId, month);
//    }
}
