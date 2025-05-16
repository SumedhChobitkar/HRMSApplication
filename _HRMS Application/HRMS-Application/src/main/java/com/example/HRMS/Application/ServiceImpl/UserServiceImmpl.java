package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.Role;
import com.example.HRMS.Application.Entity.User;
import com.example.HRMS.Application.Repository.UserRepository;
import com.example.HRMS.Application.Service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImmpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImmpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

//    @Override
//    public User register(User user) {
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already registered");
//        }
//
//        if (user.getRole() == null) {
//            user.setRole(Role.USER); // Default to USER role if none provided
//        }
//        user.setPassword(encoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }

    @Override
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && encoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}



