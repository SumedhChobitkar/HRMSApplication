package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.CommonUtil.ValidationClass;

import com.example.HRMS.Application.Entity.User;
import com.example.HRMS.Application.Repository.UserRepository;
import com.example.HRMS.Application.Service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImmpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImmpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }
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

@Override
public void updatePassword(Long userId, String newPassword) {
    if (!ValidationClass.PASSWORD_PATTERN.matcher(newPassword).matches()) {
        throw new IllegalArgumentException("Invalid password");
    }

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));

    user.setPassword(encoder.encode(newPassword));
    userRepository.save(user);
}


    @Override
    public void updateProfilePicture(Long userId, MultipartFile profilePicture) throws IOException {
        if (profilePicture == null || profilePicture.isEmpty()) {
            throw new IllegalArgumentException("Profile picture is required");
        }

        String contentType = profilePicture.getContentType();
        if (!isValidImageType(contentType)) {
            throw new IllegalArgumentException("Only JPEG and PNG are allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setProfilePicture(profilePicture.getBytes());
        userRepository.save(user);
    }

    @Override
    public Optional<byte[]> getProfilePictureByIdOrEmail(Long userId, String email) {
        Optional<User> userOptional = Optional.empty();

        if (userId != null) {
            userOptional = userRepository.findById(userId);
        } else if (email != null && !email.isEmpty()) {
            userOptional = userRepository.findByEmail(email);
        }

        if (userOptional.isPresent() && userOptional.get().getProfilePicture() != null) {
            return Optional.of(userOptional.get().getProfilePicture());
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/jpg")
        );
    }


    }







