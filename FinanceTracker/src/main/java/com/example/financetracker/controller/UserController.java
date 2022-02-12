package com.example.financetracker.controller;


import com.example.financetracker.model.dto.*;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")    // ?
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserRegisterResponseDTO register(@RequestBody UserRegisterRequestDTO requestDTO) {
        return userService.addUser(requestDTO);
    }

    @PostMapping("/login")
    public UserLoginResponseDTO login(@RequestBody UserLoginRequestDTO requestDTO, HttpSession session){
        //todo watch lecture
        UserLoginResponseDTO response = userService.login(requestDTO);
        session.setAttribute("LoggedUser", response.getId());
        session.setMaxInactiveInterval(60*30);
        return response;
    }

    @PutMapping("/editprofile")
    public UserProfileDTO editProfile(@RequestBody UserProfileDTO requestDTO){
         return userService.editProfile(requestDTO);
    }





}
