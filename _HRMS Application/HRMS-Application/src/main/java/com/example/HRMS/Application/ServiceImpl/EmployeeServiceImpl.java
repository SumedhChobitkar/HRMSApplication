package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Exception.EmployeeNotFoundException;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee addEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            logger.warn("Attempt to add employee with existing email: {}", employee.getEmail());
            throw new RuntimeException("Email already exists");
        }
        logger.info("Adding employee with email: {}", employee.getEmail());
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        logger.info("Updating employee ID: {}", id);

        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setEmail(employee.getEmail());
        existing.setPhone(employee.getPhone());
        existing.setDepartment(employee.getDepartment());
        existing.setJobTitle(employee.getJobTitle());
        existing.setRole(employee.getRole());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setExitDate(employee.getExitDate());
        existing.setStatus(employee.getStatus());

        // Update profile picture
        existing.setProfilePicture(employee.getProfilePicture());

        return employeeRepository.save(existing);
    }


//    @Override
//    public Employee updateEmployee(Long id, Employee employee) {
//        Employee existing = employeeRepository.findById(id)
//                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
//        logger.info("Updating employee ID: {}", id);
//
//        existing.setFirstName(employee.getFirstName());
//        existing.setLastName(employee.getLastName());
//        existing.setEmail(employee.getEmail());
//        existing.setPhone(employee.getPhone());
//        existing.setDepartment(employee.getDepartment());
//        existing.setJobTitle(employee.getJobTitle());
//        existing.setRole(employee.getRole());
//        existing.setJoiningDate(employee.getJoiningDate());
//        existing.setExitDate(employee.getExitDate());
//        existing.setStatus(employee.getStatus());
//
//        return employeeRepository.save(existing);
//    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            logger.error("Attempt to delete non-existing employee ID: {}", id);
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
        logger.info("Deleted employee ID: {}", id);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        logger.info("Fetching employee by ID: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

}
