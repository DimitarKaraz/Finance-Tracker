package com.example.financetracker.controller;


import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dto.ResponseWrapper;
import com.example.financetracker.model.dto.userDTOs.ChangePasswordRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserEditProfileRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.service.FileService;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getProfile() {
        return ResponseWrapper.wrap("User retrieved.",
                userService.getProfile(), HttpStatus.OK);
    }

    @PutMapping("/edit_profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> editProfile(@Valid @RequestBody UserEditProfileRequestDTO requestDTO) {
        return ResponseWrapper.wrap("Profile was edited.",
                userService.editProfile(requestDTO), HttpStatus.OK);
    }

    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO){
        userService.changePassword(requestDTO);
        return ResponseEntity.ok().body("message: Password was changed successfully!\n" + "timestamp: "
                + LocalDateTime.now());
    }

    @PostMapping("/change_profile_image")
    public ResponseEntity<ResponseWrapper<String>> changeProfileImage(@RequestParam(name = "file") MultipartFile file){
        if (file.getContentType() == null || !file.getContentType().matches(FileService.allowedContentTypesREGEX)){
            throw new BadRequestException("Unsupported file type.");
        }
        return ResponseWrapper.wrap("Uploaded profile image:", userService.uploadProfileImage(file), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<String> deleteUser() {
        userService.deleteUser();
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        new SecurityContextLogoutHandler().logout(request, null, null);
        return ResponseEntity.ok().body("message: Your profile was deleted. We will miss you!\n" + "timestamp: "
                + LocalDateTime.now());
    }

    @GetMapping("/admin/all_users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<List<UserProfileDTO>>> getAllUsers() {
        return ResponseWrapper.wrap("All users retrieved.", userService.getAllUsers(), HttpStatus.OK);
    }


}


