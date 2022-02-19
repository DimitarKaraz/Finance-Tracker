package com.example.financetracker.controller;


import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.userDTOs.ChangePasswordRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserLoginRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.model.dto.userDTOs.UserRegisterRequestDTO;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register(final Model model){
        model.addAttribute("userRegisterRequestDTO", new UserRegisterRequestDTO());
        return "redirect:/registerform";
    }

    @PostMapping("/register_user")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> register(@Valid @RequestBody UserRegisterRequestDTO requestDTO) {
                //TODO: security
        return ResponseWrapper.wrap("User was registered.", userService.register(requestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> login(@Valid @RequestBody UserLoginRequestDTO requestDTO) {
        //TODO: security
        UserProfileDTO response = userService.login(requestDTO);
        return ResponseWrapper.wrap("User logged in.", response, HttpStatus.OK);
    }

    @PutMapping("/edit_profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> editProfile(@Valid @RequestBody UserProfileDTO requestDTO) {
        //TODO: SECURITY
        return ResponseWrapper.wrap("Profile was edited.", userService.editProfile(requestDTO), HttpStatus.OK);
    }


    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO){
        //TODO: SECURITY -> LOG USER OUT and return OK:
        //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
        //sss.logout(...);
        userService.changePassword(requestDTO);
        return ResponseEntity.ok().body("Password was changed.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getUserById(@PathVariable int id) {
        //TODO: SECURITY (-> Only for users with the same id??)
        return ResponseWrapper.wrap("User retrieved.", userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/all_users")
    public ResponseEntity<ResponseWrapper<List<UserProfileDTO>>> getAllUsers() {
        //TODO: SECURITY -> Only for ROLE_ADMIN
        return ResponseWrapper.wrap("All users retrieved.", userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete_user")
    public ResponseEntity<String> deleteUserById(@PathVariable int id) {
        //TODO: SECURITY -> LOG USER OUT:
        //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
        //sss.logout(...);
        userService.deleteUserById(id);
        return ResponseEntity.ok().body("\"message\": \"Your profile was deleted. We will miss you!\"\n" + "\"timestamp\": " + LocalDateTime.now());
    }

}


