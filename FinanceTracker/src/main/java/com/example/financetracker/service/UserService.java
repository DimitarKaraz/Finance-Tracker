package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.FileTransferException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.userDTOs.ChangePasswordRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserEditProfileRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder encoder;

    public UserProfileDTO getProfile() {
        User user = userRepository.findById(MyUserDetailsService.getCurrentUserId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        return modelMapper.map(user, UserProfileDTO.class);
    }

    public UserProfileDTO editProfile(UserEditProfileRequestDTO requestDTO){
        User user = userRepository.findById(MyUserDetailsService.getCurrentUserId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setGender(requestDTO.getGender().toLowerCase());
        user.setDateOfBirth(requestDTO.getDateOfBirth());
        userRepository.save(user);

        return modelMapper.map(user, UserProfileDTO.class);
    }

    public void changePassword(ChangePasswordRequestDTO requestDTO){
        User user =  userRepository.findById(MyUserDetailsService.getCurrentUserId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        if (!encoder.matches(requestDTO.getOldPassword(), user.getPassword())){
            throw new BadRequestException(("Wrong password."));
        }
        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmNewPassword())){
            throw new BadRequestException("Passwords do not match.");
        }
        user.setPassword(encoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
    }

    public String uploadProfileImage(MultipartFile file) {
        User user = userRepository.findById(MyUserDetailsService.getCurrentUserId())
                .orElseThrow(() -> {throw new NotFoundException("User not found.");});
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || !extension.matches(FileService.allowedExtensionsREGEX)){
            throw new BadRequestException("Unsupported file type.");
        }
        String fileName = UUID.randomUUID() + "." + extension;
        try {
            Files.copy(file.getInputStream(), new File(FileService.PROFILE_IMAGES_PATH + File.separator + fileName).toPath());
            String oldImageUrl = user.getProfileImageUrl();
            File oldFile = new File(FileService.PROFILE_IMAGES_PATH+File.separator+oldImageUrl);
            oldFile.delete();
        } catch (IOException e) {
            throw new FileTransferException("File upload failed.");
        }
        user.setProfileImageUrl(fileName);
        userRepository.save(user);
        return fileName;
    }

    public void deleteUser() {
        int id = MyUserDetailsService.getCurrentUserId();
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User does not exist.");
        }
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserProfileDTO> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(user -> modelMapper.map(user, UserProfileDTO.class))
                .collect(Collectors.toList());
    }

}