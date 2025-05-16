package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.Employee;
import com.example.HRMS.Application.Entity.Role;
import com.example.HRMS.Application.Entity.User;
import com.example.HRMS.Application.Security.JwtService;

import com.example.HRMS.Application.Service.EmployeeService;
import com.example.HRMS.Application.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Employee")
public class UserController {

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




//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody User user) {
//        User registered = userService.register(user);
//        return ResponseEntity.ok(registered);
//    }
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
    String email = (String) payload.get("email");
    String password = (String) payload.get("password");
    String roleStr = (String) payload.get("role");
    Long employeeId = payload.get("employeeId") != null ? Long.valueOf(payload.get("employeeId").toString()) : null;

    Role role = Role.valueOf(roleStr);

    if (employeeId == null) {
        return ResponseEntity.badRequest().body("EmployeeId is required");
    }

    Optional<Employee> employeeOpt = employeeService.findById(employeeId);
    if (employeeOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Employee profile not found");
    }

    Employee employee = employeeOpt.get();

    if (!employee.getRole().equals(role)) {
        return ResponseEntity.badRequest().body("Role mismatch between User and Employee");
    }

    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(role);
    user.setEmployee(employee);

    User registeredUser = userService.register(user);
    return ResponseEntity.ok(registeredUser);
}


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        Optional<User> user = userService.login(creds.get("email"), creds.get("password"));
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }

        String token = jwtUtil.generateToken(user.get().getEmail(), user.get().getRole().name());

        return ResponseEntity.ok(Collections.singletonMap("token", token));
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