package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee addEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);
    Employee getEmployeeById(Long id);
    List<Employee> getAllEmployees();

    Optional<Employee> findById(Long id);

    void deleteEmployeeByEmail(String email);


}
