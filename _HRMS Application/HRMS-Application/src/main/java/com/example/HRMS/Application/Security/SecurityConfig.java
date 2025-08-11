package com.example.HRMS.Application.Security;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // ✅ enable cors
                .cors(cors ->{})
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/Employee/login",
                                "/api/Employee/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/leaves/**",
                                "/api/attendance/**",
                                "/api/helpdesk/**",
//                                "/api/attendance/signIn",
//                                "/api/attendance/signOut",
//                                "/api/attendance/getAttendanceByid/{id}",

                                "/api/salary/id/{id}",
                                "/api/salary/month/{month}",
                                "/swagger-ui.html",
                                "/api/reviews/**",
                                "/api/holidays/id/{id}",
                                "/api/holidays",
                                "/api/calendar-days/**",
                                "/Employee/update-password",
                                "/Employee/update-profile-picture",
                                "/api/tasks/**",
                                "/api/regularization-and-permission/**",
                                "/api/regularization-and-permission/permissions/{employeeId}",
                                "/api/regularization-and-permission/regularizations/{employeeId}",
                                "/api/forgotPassword/**"

                        ).permitAll()

                        // Swagger
                       // .requestMatchers("/HRMS-Application/**").authenticated()

                        .requestMatchers("/api/Employee/user/**").hasRole("USER")
                        .requestMatchers("/api/Employee/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/Employee/hr/**").hasRole("HR")
                        .requestMatchers("/api/Employee/seniorhr/**").hasRole("SENIOR_HR")
                        .requestMatchers("/api/employees/**").hasAnyRole("HR", "SENIOR_HR", "MANAGER","USER")
                        //Payroll
                        .requestMatchers("/api/salary/upload").hasAnyRole("HR", "SENIOR_HR")
                        .requestMatchers("/api/salary/all").hasAnyRole("HR", "SENIOR_HR")
                        .requestMatchers("/api/salary/delete/{id}").hasAnyRole( "HR", "SENIOR_HR")
                        .requestMatchers("/api/salary/id/**").hasAnyRole("HR", "SENIOR_HR", "MANAGER","USER")
                       .requestMatchers("/api/salary/email/**").hasAnyRole("HR", "SENIOR_HR", "MANAGER","USER")
                        .requestMatchers("/api/salary/Email/month").hasAnyRole("HR", "SENIOR_HR", "MANAGER", "USER")


                        //Attendance
                        .requestMatchers("/api/attendance/getAllAttendance").hasAnyRole( "HR", "SENIOR_HR")
                        .requestMatchers("/api/attendance/updateAttendance/{id}").hasAnyRole( "HR", "SENIOR_HR")
                        .requestMatchers("/api/attendance/deleteAttendanceById/{id}").hasAnyRole( "HR", "SENIOR_HR")

                        //PerformanceReview
                        .requestMatchers("/api/reviews/createReview").hasAnyRole( "MANAGER")
                        .requestMatchers("/api/reviews/getAllReviews").hasAnyRole( "HR", "SENIOR_HR","MANAGER")
                        .requestMatchers("/api/reviews/getReviewByEmployeeId/{employeeId}").hasAnyRole( "HR", "SENIOR_HR","MANAGER")
                        .requestMatchers("/api/reviews/getReviewByEmail/{email}").hasAnyRole( "HR", "SENIOR_HR","MANAGER")
                        .requestMatchers("/api/reviews/updateReview/{id}").hasAnyRole( "MANAGER")
                        .requestMatchers("/api/reviews/DeleteById/{id}").hasAnyRole( "HR", "SENIOR_HR","MANAGER")

                        //Regularization and Permission
                        .requestMatchers("/api/regularization-and-permission/approve/{requestId}").hasAnyRole( "HR","MANAGER")
                        .requestMatchers("/api/regularization-and-permission/reject/{requestId}").hasAnyRole( "HR","MANAGER")
                        .requestMatchers("/api/regularization-and-permission/pending-requests").hasAnyRole( "HR","MANAGER")
                       // .requestMatchers("/api/regularization-and-permission//permissions/{employeeId}").hasAnyRole( "HR","MANAGER")
                        //.requestMatchers("/api/regularization-and-permission/regularizations/{employeeId}").hasAnyRole( "HR","MANAGER")


                        //Holiday



                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // ✅ CORS Configuration Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // Optional, if using cookies/auth tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
