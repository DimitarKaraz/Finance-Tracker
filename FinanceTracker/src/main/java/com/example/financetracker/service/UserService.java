package com.example.financetracker.service;

import com.example.financetracker.exceptions.AuthenticationException;
import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.*;
import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.utilities.email_validator.EmailValidator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder encoder;

    public UserRegisterResponseDTO addUser(UserRegisterRequestDTO requestDTO) {
        if (!EmailValidator.validateEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Please enter a valid email!");
        }

        if (userRepository.findByEmail(requestDTO.getEmail()) != null) {
            throw new BadRequestException("Email already exists.");
        }
        if (!requestDTO.getConfirmPassword().equals(requestDTO.getPassword())){
            throw new BadRequestException("Password does not match.");
        }
        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
        User userPojo = modelMapper.map(requestDTO, User.class);
        userRepository.save(userPojo);
        return modelMapper.map(userPojo, UserRegisterResponseDTO.class);
    }

    public UserLoginResponseDTO login(UserLoginRequestDTO requestDTO){
        User userPojo = userRepository.findByEmail(requestDTO.getEmail());
        if (userPojo == null || !(encoder.matches(requestDTO.getPassword(), userPojo.getPassword()))){
            throw new AuthenticationException("Wrong email or password.");
        }
        return modelMapper.map(userPojo, UserLoginResponseDTO.class);
    }

    public UserProfileDTO editProfile(UserProfileDTO requestDTO){
        User user = userRepository.getById(requestDTO.getUserId());
        user.setDateOfBirth(requestDTO.getDateOfBirth());
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setGender(requestDTO.getGender().name());
        user.setProfileImageUrl(requestDTO.getProfileImageUrl());
        userRepository.save(user);
        return modelMapper.map(user, UserProfileDTO.class);

    }

    public UserProfileDTO getUser(int id) {
        User userPojo = userRepository.findByUserId(id);
        if (userPojo == null) {
            throw new NotFoundException("User does not exist.");
        }
        return modelMapper.map(userPojo, UserProfileDTO.class);
    }

    public List<UserProfileDTO> getAllUser() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(user -> modelMapper.map(user, UserProfileDTO.class))
                .collect(Collectors.toList());
    }

    public void changePassword(ChangePasswordRequestDTO requestDTO){
        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmNewPassword())){
            throw new BadRequestException("Passwords do not match.");
        }
        User user = userRepository.getById(requestDTO.getUserId());
        if (!encoder.matches(requestDTO.getOldPassword(), user.getPassword())){
            throw new BadRequestException(("Wrong password."));
        }
        user.setPassword(encoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User does not exist.");
        }
        userRepository.deleteById(id);
        if (userRepository.existsById(id)) {
            throw new NotFoundException("Failed to delete user.");
        }
    }
}