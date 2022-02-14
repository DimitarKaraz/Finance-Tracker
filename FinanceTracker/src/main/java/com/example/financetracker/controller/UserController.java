package com.example.financetracker.controller;


import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.userDTOs.*;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> register(@RequestBody UserRegisterRequestDTO requestDTO) {
        ResponseWrapper<UserProfileDTO> wrapper = new ResponseWrapper<>("User was registered.", userService.addUser(requestDTO), HttpStatus.CREATED, LocalDateTime.now());
        return ResponseEntity.status(201).body(wrapper);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> login(@Valid @RequestBody UserLoginRequestDTO requestDTO, HttpSession session) {
        UserProfileDTO response = userService.login(requestDTO);
        session.setAttribute("LoggedUser", response.getUserId());
        session.setMaxInactiveInterval(60 * 30);
        ResponseWrapper<UserProfileDTO> wrapper = new ResponseWrapper<>("User was registered.", response, HttpStatus.OK, LocalDateTime.now());
        return ResponseEntity.ok().body(wrapper);
    }


    @PutMapping("/{id}/edit_profile")
    public UserProfileDTO editProfile(@RequestBody UserProfileDTO requestDTO) {
        //TODO: check if request is valid with Interceptor (valid session, valid id input)
        return userService.editProfile(requestDTO);
    }


    @PutMapping("/{id}/change_password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO){
        //TODO: check if request is valid with Interceptor (valid session, valid id input)
        //todo maybe change return to ChangePasswordResponseDTO
        userService.changePassword(requestDTO);
        return ResponseEntity.ok().body("Password was changed successfully.");
    }

    @GetMapping("/{id}")
    public UserProfileDTO getUserById(@PathVariable int id) {
        //TODO: check if request is valid with Interceptor (valid session, valid id input)
        return userService.getUser(id);
    }

    @GetMapping()
    public List<UserProfileDTO> getAllUsers() {
        //TODO: check if request is valid with Interceptor (valid session)
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUserById(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body("Your profile was deleted. We will miss you!");
    }

}


