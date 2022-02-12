package com.example.financetracker.controller;


import com.example.financetracker.model.dto.*;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserRegisterResponseDTO register(@RequestBody UserRegisterRequestDTO requestDTO) {
        return userService.addUser(requestDTO);
    }

    @PostMapping("/login")
    public UserLoginResponseDTO login(@RequestBody UserLoginRequestDTO requestDTO, HttpSession session) {
        UserLoginResponseDTO response = userService.login(requestDTO);
        session.setAttribute("LoggedUser", response.getId());
        session.setMaxInactiveInterval(60 * 30);
        return response;
    }


    @PutMapping("/edit_profile")
    public UserProfileDTO editProfile(@RequestBody UserProfileDTO requestDTO) {
        return userService.editProfile(requestDTO);
    }

    @GetMapping("/{id}")
    public UserProfileDTO getUserById(@PathVariable int id) {
        //TODO: check if request is valid with Interceptor (valid session, valid id input)
        return userService.getUser(id);
    }

    @GetMapping()
    public List<UserProfileDTO> getAllUsers() {
        //TODO: check if request is valid with Interceptor (valid session)
        return userService.getAllUser();
    }

    @PutMapping("/change_password")
    public String changePassword(@RequestBody ChangePasswordRequestDTO requestDTO){
        userService.changePassword(requestDTO);
        //todo change return

        return "Password changed.";
    }

}


