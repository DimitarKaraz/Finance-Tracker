package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.model.dto.UserRegisterRequestDTO;
import com.example.financetracker.model.dto.UserRegisterResponseDTO;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.utilities.email_validator.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder encoder;

    public UserRegisterResponseDTO addUser(UserRegisterRequestDTO requestDTO) {
        //TODO: validate email
        // and passwords

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
        User user = userRepository.findByEmail(requestDTO.getEmail)
    }


}
