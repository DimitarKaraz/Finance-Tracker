package com.example.financetracker.controller;

import com.example.financetracker.model.User;
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


    @PostMapping("/register")
    public User register(@RequestBody User user) {


    }



    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
