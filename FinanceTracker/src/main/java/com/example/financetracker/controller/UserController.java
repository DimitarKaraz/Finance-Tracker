package com.example.financetracker.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")    // ?
public class UserController {

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping
    public User register() {

    }



    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
