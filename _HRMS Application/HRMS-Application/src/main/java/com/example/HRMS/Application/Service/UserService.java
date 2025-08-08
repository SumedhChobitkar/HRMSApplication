package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.Role;
import com.example.HRMS.Application.Entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User register(User user);
    Optional<User> login(String email, String password);

    public Optional<User>  findByEmail(String email);
    void updatePassword(Long userId, String newPassword);
    void updateProfilePicture(Long userId, MultipartFile profilePicture) throws IOException;
    Optional<byte[]> getProfilePictureByIdOrEmail(Long userId, String email);
    Optional<User> findById(Long id);
    public List<User> getUsersByRoles(List<Role> roles);
    public List<User> getCcSuggestions(String query, List<Role> roles) ;




}
