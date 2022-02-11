package com.example.financetracker.controller;

import com.example.financetracker.model.User;
import com.example.financetracker.model.dto.UserRegisterRequestDTO;
import com.example.financetracker.model.dto.UserRegisterResponseDTO;
import com.example.financetracker.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")    // ?
public class UserController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping()
    public UserRegisterResponseDTO register(@RequestBody UserRegisterRequestDTO requestDTO) {
        return userService.addUser(requestDTO);

    }


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
