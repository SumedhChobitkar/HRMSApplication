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


    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/Employee/login",
                                "/Employee/register",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/Employee/user/**").hasRole("USER")
                        .requestMatchers("/Employee/manager/**").hasRole("MANAGER")
                        .requestMatchers("/Employee/hr/**").hasRole("HR")
                        .requestMatchers("/Employee/seniorhr/**").hasRole("SENIOR_HR")
                        .requestMatchers("/api/employees/**").hasAnyRole("HR", "SENIOR_HR", "MANAGER")
                        //Payroll
                        .requestMatchers("/api/salary/**").hasAnyRole("HR", "SENIOR_HR", "MANAGER")
                        .requestMatchers("/api/salary/history/**", "/api/salary/slip/**").hasAnyRole("USER", "HR", "SENIOR_HR")


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
