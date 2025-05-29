package com.example.HRMS.Application.Repository;

import com.example.HRMS.Application.Entity.BaseSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseSalaryRepository extends JpaRepository<BaseSalary, Long> {
    Optional<BaseSalary> findByEmployeeId(Long employeeId);
}
