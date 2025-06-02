package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

    Optional<SalaryRecord> findByFileName(String fileName);
    List<SalaryRecord> findAllByMonth(String month);
    List<SalaryRecord> findByUserName(String userName);

    // You can add methods to find by employee if needed
    List<SalaryRecord> findByEmployee(Employee employee);
//    Optional<SalaryRecord> findByEmployeeIdAndMonth(Long empId, String month);
}
