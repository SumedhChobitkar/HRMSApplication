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
            String email = "manager@gmail.com";

            if (userRepository.findByEmail(email).isPresent()) {
                System.out.println("Manager user already exists.");
                return;
            }

            Employee manager = new Employee();
            manager.setFirstName("Sumedh");
            manager.setLastName("Manager");
            manager.setEmail(email);
            manager.setPhone("9999999999");
            manager.setDepartment("Admin");
            manager.setJobTitle("Manager");
            manager.setRole(Role.MANAGER);
            manager.setJoiningDate(LocalDate.now());
            manager.setStatus("Active");

            Employee savedManager = employeeRepository.save(manager);

            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("Manager@123")); // password
            user.setRole(Role.MANAGER);
            user.setEmployee(savedManager);

            userRepository.save(user);

            System.out.println("Default MANAGER user created: " + email + " / password: Manager@123");
        };
    }
}
