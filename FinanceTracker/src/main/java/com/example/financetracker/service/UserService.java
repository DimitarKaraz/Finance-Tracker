package com.example.financetracker.service;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.dto.userDTOs.ChangePasswordRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserLoginRequestDTO;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.model.dto.userDTOs.UserRegisterRequestDTO;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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

    public UserProfileDTO register(UserRegisterRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }
        if (!requestDTO.getConfirmPassword().equals(requestDTO.getPassword())){
            throw new BadRequestException("Password does not match.");
        }
        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
        User user = modelMapper.map(requestDTO, User.class);
        user.setAuthorities("ROLE_USER");
        userRepository.save(user);
        return modelMapper.map(user, UserProfileDTO.class);
    }

    public UserProfileDTO login(UserLoginRequestDTO requestDTO){
        User user = userRepository.findByEmail(requestDTO.getEmail());
        if (user == null || !(encoder.matches(requestDTO.getPassword(), user.getPassword()))){
            throw new UnauthorizedException("Wrong email or password.");
        }
        return modelMapper.map(user, UserProfileDTO.class);
    }

    public UserProfileDTO editProfile(UserProfileDTO requestDTO){
        User userBefore = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        //TODO: check is userId == session.userId
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User userAfter = modelMapper.map(requestDTO, User.class);
        userAfter.setGender(requestDTO.getGender().toLowerCase());
        userAfter.setPassword(userBefore.getPassword());
        userAfter.setAuthorities(userBefore.getAuthorities());
        userRepository.save(userAfter);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(userAfter, UserProfileDTO.class);
    }

    public UserProfileDTO getUserById(int id) {
        //TODO: check is userId == session.userId
        User user = userRepository.findById(id)
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        return modelMapper.map(user, UserProfileDTO.class);
    }

    public List<UserProfileDTO> getAllUsers() {
        //TODO: check if ROLE_ADMIN
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(user -> modelMapper.map(user, UserProfileDTO.class))
                .collect(Collectors.toList());
    }

    public void changePassword(ChangePasswordRequestDTO requestDTO){
        //TODO: user must be logged in
        User user =  userRepository.findById(requestDTO.getUserId())
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

    public void deleteUserById(int id) {
        //TODO: check is userId == session.userId
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User does not exist.");
        }
        userRepository.deleteById(id);
    }

    @SneakyThrows
    public String uploadFile(MultipartFile file, int userId) {
       /*
       HttpServletRequest request
       UserController.validateLogin(request.getSession(), request);
        int loggedUserId = (int) request.getSession().getAttribute(UserController.USER_ID);*/
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        //todo implement better random name generation
        String fileName = System.nanoTime() + "." + extension;
        Files.copy(file.getInputStream(), new File("profileImages" + File.separator + fileName).toPath());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {throw new NotFoundException("User not found.");});
        user.setProfileImageUrl(fileName);
        userRepository.save(user);
        return fileName;
    }
}