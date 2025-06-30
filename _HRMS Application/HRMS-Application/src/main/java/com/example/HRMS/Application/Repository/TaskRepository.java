package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.SalaryRecord;
import com.example.HRMS.Application.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByEmployeeId(Long employeeId);
    List<Task> findByEmployee_FirstNameAndEmployee_LastName(String firstName, String lastName);
}
