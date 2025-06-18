package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.User;
import com.example.HRMS.Application.Security.JwtService;

import com.example.HRMS.Application.Service.EmployeeService;
import com.example.HRMS.Application.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/Employee")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtService jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private EmployeeService employeeService;

    public UserController(UserService userService, JwtService jwtUtil, PasswordEncoder passwordEncoder,EmployeeService employeeService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.employeeService = employeeService;
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam("userData") String userData,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();

        try {
            User user = objectMapper.readValue(userData, User.class);

            // Validate essential fields
            if (user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                    user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                    user.getRole() == null) {
                return ResponseEntity.badRequest().body("Email, password, and role are required.");
            }

            // Check employeeId if needed
            if (user.getEmployee() == null || user.getEmployee().getId() == null) {
                return ResponseEntity.badRequest().body("EmployeeId is required");
            }

            Optional<Employee> employeeOpt = employeeService.findById(user.getEmployee().getId());
            if (employeeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Employee profile not found");
            }

            if (!employeeOpt.get().getRole().equals(user.getRole())) {
                return ResponseEntity.badRequest().body("Role mismatch between User and Employee");
            }

            if (userService.findByEmail(user.getEmail()).isPresent()) {
                response.put("message", "Email already registered");
                return ResponseEntity.badRequest().body(response);
            }

            if (profilePicture != null && !profilePicture.isEmpty()) {
                String contentType = profilePicture.getContentType();
                if (isValidImageType(contentType)) {
                    user.setProfilePicture(profilePicture.getBytes());
                } else {
                    response.put("message", "Invalid profile picture format. Only JPEG and PNG are supported.");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmployee(employeeOpt.get());

            User savedUser = userService.register(user);
            return ResponseEntity.ok(savedUser);

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid user data format.");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing profile picture.");
        }
    }
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/jpg")
        );
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        Optional<User> user = userService.login(creds.get("email"), creds.get("password"));
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }

        Employee employee=new Employee();
        User loggedInUser = user.get();
        String token = jwtUtil.generateToken(loggedInUser.getEmail(), loggedInUser.getRole().name());

        String fullName = (loggedInUser.getFirstName() != null ? loggedInUser.getFirstName() : "") +
                " " +
                (loggedInUser.getLastName() != null ? loggedInUser.getLastName() : "");

        Map<String, Object> response = new HashMap<>();
        response.put("id", loggedInUser.getId());
        response.put("EmployeeId",loggedInUser.getEmployee().getId());
        response.put("name", fullName.trim());
        response.put("email", loggedInUser.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestParam Long userId,
                                            @RequestParam String newPassword) {
        try {
            userService.updatePassword(userId, newPassword);
            return ResponseEntity.ok("Password updated successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PutMapping("/update-profile-picture")
    public ResponseEntity<?> updateProfilePicture(@RequestParam Long userId,
                                                  @RequestParam MultipartFile profilePicture) {
        try {
            userService.updateProfilePicture(userId, profilePicture);
            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @GetMapping("/Employee/user")
@PreAuthorize("hasRole('USER')")
public  ResponseEntity<?> userOnly(){
        return ResponseEntity.ok("Welcome User");
}
    @GetMapping("/hr/dashboard")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> hrOnly() {
        return ResponseEntity.ok("Welcome HR");
    }

    @GetMapping("/manager/dashboard")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> managerOnly() {
        return ResponseEntity.ok("Welcome Manager");
    }

    @GetMapping("/seniorhr/dashboard")
    @PreAuthorize("hasRole('SENIORHR')")
    public ResponseEntity<?> seniorHrOnly() {
        return ResponseEntity.ok("Welcome Senior HR");
    }
}