package com.example.financetracker.controller;


import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.userDTOs.ChangePasswordRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserEditProfileRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.service.MyUserDetailsService;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

  /*  @GetMapping("/checky")
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

    }*/

//    @PostMapping("/register_user")
//    public ResponseEntity<ResponseWrapper<UserProfileDTO>> register(@Valid @RequestBody UserRegisterRequestDTO requestDTO) {
//        return ResponseWrapper.wrap("User was registered.", userService.register(requestDTO), HttpStatus.CREATED);
//    }

//    @PostMapping("/login")
//    public ResponseEntity<ResponseWrapper<UserProfileDTO>> login(@Valid @RequestBody UserLoginRequestDTO requestDTO) {
//        UserProfileDTO response = userService.login(requestDTO);
//        return ResponseWrapper.wrap("User logged in.", response, HttpStatus.OK);
//    }


    @GetMapping("/profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getProfile() {
        return ResponseWrapper.wrap("User retrieved.",
                userService.getProfile(), HttpStatus.OK);
    }
    // You can't see other people's profiles
/*    @GetMapping("/{id}")
    @PreAuthorize("#id == principal.userId")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getUserById(@PathVariable("id") int id) {
        return ResponseWrapper.wrap("User retrieved.", userService.getUserById(id), HttpStatus.OK);
    }*/


    @PutMapping("/edit_profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> editProfile(@Valid @RequestBody UserEditProfileRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Profile was edited.",
                userService.editProfile(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO){
        userService.changePassword(requestDTO);
        return ResponseEntity.ok().body("Password was changed.");
    }

    @PostMapping("/change_profile_image")
    public String changeProfileImage(@RequestParam(name = "file") MultipartFile file){
        return userService.uploadProfileImage(file);
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<String> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok().body("\"message\": \"Your profile was deleted. We will miss you!\"\n" + "\"timestamp\": "
                + LocalDateTime.now());
    }


    @GetMapping("/all_users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<List<UserProfileDTO>>> getAllUsers() {
        return ResponseWrapper.wrap("All users retrieved.", userService.getAllUsers(), HttpStatus.OK);
    }


}


