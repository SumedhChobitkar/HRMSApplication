package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.CommonUtil.ValidationClass;
import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Exception.EmployeeNotFoundException;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Service.EmployeeService;
import jakarta.transaction.Transactional;
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
        validateUserData(employee);
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            logger.warn("Attempt to add employee with existing email: {}", employee.getEmail());
            throw new RuntimeException("Email already exists");
        }
        logger.info("Adding employee with email: {}", employee.getEmail());
        return employeeRepository.save(employee);
    }

//    @Override
//    public Employee updateEmployee(Long id, Employee employee) {
//        validateUserData(employee);
//        Employee existing = employeeRepository.findById(id)
//                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
//        logger.info("Updating employee ID: {}", id);
//
//        Optional<Employee> employeeWithEmail = employeeRepository.findByEmail(employee.getEmail());
//        if (employeeWithEmail.isPresent() && !employeeWithEmail.get().getId().equals(id)) {
//            throw new IllegalArgumentException("Email already exists for another employee.");
//        }
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
//        existing.setGender(employee.getGender());
//
//        // Update profile picture
//        existing.setProfilePicture(employee.getProfilePicture());
//
//        if(existing.getUser()!= null) {
//            existing.getUser().setRole(employee.getRole());
//            existing.getUser().setFirstName(employee.getFirstName());
//            existing.getUser().setLastName(employee.getLastName());
//            existing.getUser().setRole(employee.getRole());
//            existing.getUser().setEmail(employee.getEmail());
//            existing.getUser().setGender(employee.getGender());
//        }
//
//        return employeeRepository.save(existing);
//    }
@Override
public Employee updateEmployee(Long id, Employee employee) {
    validateUserData(employee);

    Employee existing = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    logger.info("Updating employee ID: {}", id);

    // Keep email static (do not update from request)
    String currentEmail = existing.getEmail();

    existing.setFirstName(employee.getFirstName());
    existing.setLastName(employee.getLastName());
    existing.setPhone(employee.getPhone());
    existing.setDepartment(employee.getDepartment());
    existing.setJobTitle(employee.getJobTitle());
    existing.setRole(employee.getRole());
    existing.setJoiningDate(employee.getJoiningDate());
    existing.setExitDate(employee.getExitDate());
    existing.setStatus(employee.getStatus());
    existing.setGender(employee.getGender());

    // Update profile picture
    existing.setProfilePicture(employee.getProfilePicture());

    // Restore old email so it doesn’t change
    existing.setEmail(currentEmail);

    if (existing.getUser() != null) {
        existing.getUser().setRole(employee.getRole());
        existing.getUser().setFirstName(employee.getFirstName());
        existing.getUser().setLastName(employee.getLastName());
        existing.getUser().setGender(employee.getGender());

        // Keep email static in User also
        existing.getUser().setEmail(currentEmail);
    }

    return employeeRepository.save(existing);
}


    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            logger.error("Attempt to delete non-existing employee ID: {}", id);
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
        logger.info("Deleted employee ID: {}", id);
    }

   /* @Override
    @Transactional
    public void deleteEmployeeByEmail(String email) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);
        if (employeeOpt.isEmpty()) {
            throw new EmployeeNotFoundException("Employee not found with email: " + email);
        }
        employeeRepository.deleteByEmail(email);
        logger.info("Deleted employee with email: {}", email);
    }*/
   @Override
   @Transactional
   public void deleteEmployeeByEmail(String email) {
       Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);
       if (employeeOpt.isEmpty()) {
           throw new EmployeeNotFoundException("Employee not found with email: " + email);
       }    employeeRepository.deleteByEmail(email);
       logger.info("Deleted employee with email: {}", email);}


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


        public static void validateUserData(Employee employee) {
            if (!ValidationClass.NAME_PATTERN.matcher(employee.getFirstName()).matches()) {
                throw new IllegalArgumentException("Invalid first name.");
            }
            if (!ValidationClass.NAME_PATTERN.matcher(employee.getLastName()).matches()) {
                throw new IllegalArgumentException("Invalid last name.");
            }
            if (!ValidationClass.EMAIL_PATTERN.matcher(employee.getEmail()).matches()) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            if (!ValidationClass.PHONE_PATTERN.matcher(employee.getPhone()).matches()) {
                throw new IllegalArgumentException("Invalid phone number.");
            }
            if (!ValidationClass.NAME_PATTERN.matcher(employee.getDepartment()).matches()) {
                throw new IllegalArgumentException("Invalid!,First character must be capital.");
            }
    }

}
