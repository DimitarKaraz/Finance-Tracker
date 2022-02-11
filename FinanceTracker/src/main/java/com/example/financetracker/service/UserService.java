package com.example.financetracker.service;

import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.model.dto.UserRegisterRequestDTO;
import com.example.financetracker.model.dto.UserRegisterResponseDTO;
import com.example.financetracker.model.pojo.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UserRegisterResponseDTO addUser(UserRegisterRequestDTO requestDTO) {
        //TODO: validate email and passwords
        if (userRepository.findByEmail(requestDTO.getEmail()) != null) {
            throw new BadRequestException("Email already exists.");
        }
        //TODO: bcrypt password
        User userPojo = modelMapper.map(requestDTO, User.class);
        userRepository.save(userPojo);
        return modelMapper.map(userPojo, UserRegisterResponseDTO.class);
    }


}
