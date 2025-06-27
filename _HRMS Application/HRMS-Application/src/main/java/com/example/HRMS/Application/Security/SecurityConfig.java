package com.example.HRMS.Application.Security;

import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    @Autowired
    private UserDetailsService userDetailsService;

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
                .cors(cors ->{})
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/Employee/login",
                                "/Employee/**",
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
                                "/api/tasks/**"

                        ).permitAll()

                        // Swagger
                       // .requestMatchers("/HRMS-Application/**").authenticated()

                        .requestMatchers("/Employee/user/**").hasRole("USER")
                        .requestMatchers("/Employee/manager/**").hasRole("MANAGER")
                        .requestMatchers("/Employee/hr/**").hasRole("HR")
                        .requestMatchers("/Employee/seniorhr/**").hasRole("SENIOR_HR")
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


                        //Holiday



                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}





//package com.example.HRMS.Application.Security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.*;
//import org.springframework.security.authentication.*;
//import org.springframework.security.config.annotation.authentication.configuration.*;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.*;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.*;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final JwtFilter jwtFilter;
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    public SecurityConfig(JwtFilter jwtFilter) {
//        this.jwtFilter = jwtFilter;
//    }
//
//    @Bean
//    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http.csrf(csrf -> csrf.disable())
////                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
////                        .requestMatchers("/Employee/register", "/Employee/login").permitAll()
////                        .requestMatchers("/Employee/manager/**").hasRole("MANAGER")
////                        .requestMatchers("/Employee/hr/**").hasRole("HR")
////                        .requestMatchers("/Employee/seniorhr/**").hasRole("SENIOR_HR")
////                        .anyRequest().authenticated()
////                );
////
////        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
////        return http.build();
////    }
//@Bean
//public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http.csrf(csrf -> csrf.disable())
//            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers("/Employee/login", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
//                    .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                    .requestMatchers("/Employee/register", "/Employee/login").permitAll()
//                    .requestMatchers("/Employee/manager/**").hasRole("MANAGER") // Only MANAGER can access this
//                    .requestMatchers("/Employee/hr/**").hasRole("HR") // Only HR can access this
//                    .requestMatchers("/Employee/seniorhr/**").hasRole("SENIOR_HR") // Only SENIOR_HR can access this
//                    .anyRequest().authenticated() // All other requests require authentication
//            )
//
////    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before the main filter
////    return http.build();
//    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .addFilterBefore( jwtFilter, UsernamePasswordAuthenticationFilter.class)
//            .userDetailsService(userDetailsService)
//            .build();
//}
//
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
