package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.SalaryRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SalaryService {


    SalaryRecord upload(MultipartFile file, String uploadedBy,String userName) throws IOException;

    List<SalaryRecord> getAllByUserEmail(String userEmail);

    SalaryRecord getById(Long id);
    //SalaryRecord getByFileName(String fileName);
    List<SalaryRecord> getByMonth(String month);
    List<SalaryRecord> getAll();
    void deleteById(Long id);
  // Optional<SalaryRecord> getByUser(String userEmail);
    public Optional<SalaryRecord> getByEmailAndMonth(String email, String month);

    // old code
//    BaseSalary assignBaseSalary(Long empId, double amount);
//    SalaryRecord generateSalary(Long empId, String month);
//    List<SalaryRecord> getSalaryHistory(Long empId);
//    Optional<SalaryRecord> getSalarySlip(Long empId, String month);
}
