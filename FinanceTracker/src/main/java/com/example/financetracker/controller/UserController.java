package com.example.financetracker.controller;


import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.userDTOs.ChangePasswordRequestDTO;
import com.example.financetracker.model.dto.userDTOs.MyUserDetails;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/checky")
    public void checkAuthentication() {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        String email = auth.getName();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        List<SimpleGrantedAuthority> authorities = auth.getAuthorities().stream()
                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                .collect(Collectors.toList()) ;
        String credentials = (String) auth.getCredentials();
        WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
        System.out.println(email);
        System.out.println(myUserDetails);
        System.out.println(authorities);
        System.out.println(credentials);
        System.out.println(details);
        System.out.println("Session id: " +details.getSessionId());
        System.out.println("Remote address: " + details.getRemoteAddress());

    }


//    @PostMapping("/register_user")
//    public ResponseEntity<ResponseWrapper<UserProfileDTO>> register(@Valid @RequestBody UserRegisterRequestDTO requestDTO) {
//                //TODO: security
//        return ResponseWrapper.wrap("User was registered.", userService.register(requestDTO), HttpStatus.CREATED);
//    }

//    @PostMapping("/login")
//    public ResponseEntity<ResponseWrapper<UserProfileDTO>> login(@Valid @RequestBody UserLoginRequestDTO requestDTO) {
//        //TODO: security
//        UserProfileDTO response = userService.login(requestDTO);
//        return ResponseWrapper.wrap("User logged in.", response, HttpStatus.OK);
//    }

    @PutMapping("/edit_profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> editProfile(@Valid @RequestBody UserProfileDTO requestDTO) {
        //TODO: SECURITY
        return ResponseWrapper.wrap("Profile was edited.", userService.editProfile(requestDTO), HttpStatus.OK);
    }


    @PutMapping("/change_password")

    @PreAuthorize("#requestDTO.userId == principal.userId")


    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO){
        //TODO: SECURITY -> LOG USER OUT and return OK:
        //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
        //sss.logout(...);
        userService.changePassword(requestDTO);
        return ResponseEntity.ok().body("Password was changed.");
    }


    //TODO:

    @GetMapping("/show_profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getUserById_GUcii(Principal principal) {
        MyUserDetails myUserDetails = (MyUserDetails) principal;
        return ResponseWrapper.wrap("User retrieved.", userService.getUserById(myUserDetails.getUserId()), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @PreAuthorize("#id == principal.userId")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getUserById(@PathVariable("id") int id) {
        return ResponseWrapper.wrap("User retrieved.", userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/all_users")
    public ResponseEntity<ResponseWrapper<List<UserProfileDTO>>> getAllUsers() {
        //TODO: SECURITY -> Only for ROLE_ADMIN
        return ResponseWrapper.wrap("All users retrieved.", userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete_user")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") int id) {
        //TODO: SECURITY -> LOG USER OUT:
        //SecurityContextLogoutHandler sss = new SecurityContextLogoutHandler();
        //sss.logout(...);
        userService.deleteUserById(id);
        return ResponseEntity.ok().body("\"message\": \"Your profile was deleted. We will miss you!\"\n" + "\"timestamp\": " + LocalDateTime.now());
    }

    @PostMapping("/{user_id}/upload_image")
    public String uploadProfileImageByUserId(@RequestParam(name = "file") MultipartFile file, @PathVariable("user_id") int userId){
        return userService.uploadFile(file, userId);
    }

}


