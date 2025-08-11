package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.ForgotPasswordOtp;
import com.example.HRMS.Application.Entity.User;
import com.example.HRMS.Application.Exception.EmployeeNotFoundException;
import com.example.HRMS.Application.Repository.ForgotPasswordOtpRepository;
import com.example.HRMS.Application.Repository.UserRepository;
import com.example.HRMS.Application.Service.EmailService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForgotPasswordOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRY_MINUTES = 5; // OTP expiration time in minutes

    public ForgotPasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Step 1: Request password reset - generates OTP and sends via email
     */
    public String requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("No User found with this email: " + email));
        return generateAndSendOtp(user);
    }

    /**
     * Step 2: Generate and send OTP
     */
    private String generateAndSendOtp(User user) {
        // Remove old OTP if exists
        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        // Generate new OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        ForgotPasswordOtp otpEntity = new ForgotPasswordOtp();
        otpEntity.setOtp(otp);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpEntity.setUser(user);

        otpRepository.save(otpEntity);

        // Send email
        emailService.sendEmail(user.getEmail(), "Password Reset OTP",
                "Your OTP for password reset is: " + otp + "\nThis OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.");

        logger.info("OTP sent to {}", user.getEmail());
        return otp;
    }

    /**
     * Step 3: Verify OTP
     */
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("No user found with email: " + email));

        ForgotPasswordOtp otpEntity = otpRepository.findByOtp(otp)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        // Check if OTP belongs to the same user
        if (!otpEntity.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("OTP does not match this user");
        }

        // Check expiry
        if (otpEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity);
            throw new IllegalArgumentException("OTP expired");
        }

        return true;
    }

    /**
     * Step 4: Reset password after OTP verification
     */
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        if (!verifyOtp(email, otp)) {
            throw new IllegalArgumentException("OTP verification failed");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("No user found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove OTP after successful password reset
        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        logger.info("Password reset successfully for {}", email);
    }
}
