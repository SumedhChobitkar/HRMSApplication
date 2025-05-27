package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.BaseSalary;
import com.example.HRMS.Application.Entity.SalaryRecord;

import java.util.List;
import java.util.Optional;

public interface SalaryService {
    BaseSalary assignBaseSalary(Long empId, double amount);
    SalaryRecord generateSalary(Long empId, String month);
    List<SalaryRecord> getSalaryHistory(Long empId);
    Optional<SalaryRecord> getSalarySlip(Long empId, String month);
}
