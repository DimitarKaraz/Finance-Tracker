package com.example.financetracker.authentication;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.model.dto.userDTOs.UserRegisterFormDTO;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder encoder;

    public UserProfileDTO register(UserRegisterFormDTO form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            System.out.println("Email already exists");
            throw new BadRequestException("Email already exists.");
        }
        if (!form.getConfirmPassword().equals(form.getPassword())){
            throw new BadRequestException("Password does not match.");
        }
        form.setPassword(encoder.encode(form.getPassword()));
        User user = modelMapper.map(form, User.class);
        user.setLastLogin(LocalDateTime.now());
        user.setAuthorities("ROLE_USER");
        userRepository.save(user);
        return modelMapper.map(user, UserProfileDTO.class);
    }

}
