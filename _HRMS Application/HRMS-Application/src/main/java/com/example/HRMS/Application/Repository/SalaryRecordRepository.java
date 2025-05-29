package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
    List<SalaryRecord> findByEmployeeId(Long employeeId);
    Optional<SalaryRecord> findByEmployeeIdAndMonth(Long empId, String month);
}
