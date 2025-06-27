package com.example.HRMS.Application.Config;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.Role;
import com.example.HRMS.Application.Entity.User;
import com.example.HRMS.Application.Repository.EmployeeRepository;
import com.example.HRMS.Application.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class StartupDataLoader {

    @Bean
    public CommandLineRunner loadInitialManager(
        EmployeeRepository employeeRepository,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String email = "superadmin@gmail.com";

            if (userRepository.findByEmail(email).isPresent()) {
                System.out.println("superAdmin  already exists.");
                return;
            }

            Employee manager = new Employee();
            manager.setFirstName("Super");
            manager.setLastName("Admin");
            manager.setEmail(email);
            manager.setPhone("1234567890");
            manager.setDepartment("Admin");
            manager.setJobTitle("Super Admin");
            manager.setRole(Role.MANAGER);
            manager.setJoiningDate(LocalDate.now());
            manager.setStatus("Active");

            Employee savedManager = employeeRepository.save(manager);

            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("Admin@123")); // password
            user.setRole(Role.MANAGER);
            user.setEmployee(savedManager);
            user.setIsregistered("TRUE");

            userRepository.save(user);

            System.out.println("Default Super admin created: " + email + " / password: Admin@123");
        };
    }
}
