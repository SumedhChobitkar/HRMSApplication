package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.ForgotPasswordOtp;
import com.example.HRMS.Application.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ForgotPasswordOtpRepository extends JpaRepository<ForgotPasswordOtp, Long> {

    Optional<ForgotPasswordOtp> findByOtp(String otp);
    Optional<ForgotPasswordOtp> findByUser(User user);

}