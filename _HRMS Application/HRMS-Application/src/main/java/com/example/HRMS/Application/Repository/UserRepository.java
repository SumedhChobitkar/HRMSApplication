package com.example.HRMS.Application.Repository;


import com.example.HRMS.Application.Entity.Role;
import com.example.HRMS.Application.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

    List<User> findByEmailContainingIgnoreCaseAndRoleIn(String email, List<Role> roles);


}

