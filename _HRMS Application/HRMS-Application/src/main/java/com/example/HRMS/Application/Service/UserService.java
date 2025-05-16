package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.User;

import java.util.Optional;

public interface UserService {

    User register(User user);
    Optional<User> login(String email, String password);

    public Optional<User>  findByEmail(String email);
}
