package com.example.financetracker.service;

import com.example.financetracker.controller.FileController;
import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.FileTransferException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.dto.userDTOs.ChangePasswordRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserEditProfileRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.model.dto.userDTOs.UserRegisterForm;
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
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder encoder;

    public UserProfileDTO register(UserRegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            System.out.println("Email already exists");
            throw new BadRequestException("Email already exists.");
        }
        if (!form.getConfirmPassword().equals(form.getPassword())){
            throw new BadRequestException("Password does not match.");
        }
        form.setPassword(encoder.encode(form.getPassword()));
        User user = modelMapper.map(form, User.class);
        user.setAuthorities("ROLE_USER");
        userRepository.save(user);
        return modelMapper.map(user, UserProfileDTO.class);
    }


//    public UserProfileDTO register(UserRegisterRequestDTO requestDTO) {
//        if (userRepository.existsByEmail(requestDTO.getEmail())) {
//            throw new BadRequestException("Email already exists.");
//        }
//        if (!requestDTO.getConfirmPassword().equals(requestDTO.getPassword())){
//            throw new BadRequestException("Password does not match.");
//        }
//        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
//        User user = modelMapper.map(requestDTO, User.class);
//        user.setAuthorities("ROLE_USER");
//        userRepository.save(user);
//        return modelMapper.map(user, UserProfileDTO.class);
//    }

//    public UserProfileDTO login(UserLoginRequestDTO requestDTO){
//        User user = userRepository.findByEmail(requestDTO.getEmail());
//        if (user == null || !(encoder.matches(requestDTO.getPassword(), user.getPassword()))){
//            throw new UnauthorizedException("Wrong email or password.");
//        }
//        return modelMapper.map(user, UserProfileDTO.class);
//    }

    public UserProfileDTO getProfile(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);
        System.out.println(userProfileDTO);
        return userProfileDTO;
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
        //TODO: password strength
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
        //todo implement better random name generation
        String fileName = System.nanoTime() + "." + extension;
        try {
            Files.copy(file.getInputStream(), new File(FileController.PROFILE_IMAGES_PATH + File.separator + fileName).toPath());
            String oldImageUrl = user.getProfileImageUrl();
            File oldFile = new File(FileController.PROFILE_IMAGES_PATH+File.separator+oldImageUrl);
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