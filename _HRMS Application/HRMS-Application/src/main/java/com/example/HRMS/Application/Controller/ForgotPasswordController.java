package com.example.HRMS.Application.Controller;

import com.example.HRMS.Application.Entity.PasswordResetRequest;
import com.example.HRMS.Application.Exception.EmployeeNotFoundException;
import com.example.HRMS.Application.ServiceImpl.ForgotPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forgotPassword")
@CrossOrigin("*")
public class ForgotPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    /**
     * Request OTP for password reset
     */
    @PostMapping("/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email) {
        logger.info("Received password reset request for email: {}", email);
        try {
            forgotPasswordService.requestPasswordReset(email);
            return ResponseEntity.ok("Password reset OTP has been sent to your email.");
        } catch (EmployeeNotFoundException e) {
            logger.error("User not found with email: {}", email);
            return ResponseEntity.status(404).body("No user found with email: " + email);
        } catch (Exception e) {
            logger.error("Error processing password reset request for email: {}", email, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    /**
     * Reset password using OTP
     */
    @PostMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable String email,
                                                @RequestBody PasswordResetRequest request) {
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String otp = request.getOtp();

        logger.info("Received request to reset password for email: {}", email);

        // Step 1: Check if passwords match
        if (!password.equals(confirmPassword)) {
            logger.error("Password reset failed: passwords do not match for email: {}", email);
            return ResponseEntity.status(400).body("Passwords do not match");
        }

        try {
            // Step 2: Reset password (this also verifies OTP inside the service)
            forgotPasswordService.resetPassword(email, otp, password);
            logger.info("Password successfully reset for email: {}", email);
            return ResponseEntity.ok("Password successfully reset");
        } catch (EmployeeNotFoundException e) {
            logger.error("No user account found for email: {}", email, e);
            return ResponseEntity.status(404).body("No user account found for email: " + email);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid or expired OTP for email: {}", email, e);
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error resetting password for email: {}", email, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }
}
