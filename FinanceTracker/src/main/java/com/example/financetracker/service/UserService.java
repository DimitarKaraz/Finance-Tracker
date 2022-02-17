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
import com.example.financetracker.utilities.email_validator.EmailValidator;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        //TODO: test regex
        if (!EmailValidator.validateEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Please enter a valid email!");
        }
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }
        if (!requestDTO.getConfirmPassword().equals(requestDTO.getPassword())){
            throw new BadRequestException("Password does not match.");
        }
        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
        User user = modelMapper.map(requestDTO, User.class);
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
        userAfter.setPassword(userBefore.getPassword());
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
        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmNewPassword())){
            throw new BadRequestException("Passwords do not match.");
        }
        User user =  userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {throw new NotFoundException("Invalid user id.");});
        //TODO: password strength
        if (!encoder.matches(requestDTO.getOldPassword(), user.getPassword())){
            throw new BadRequestException(("Wrong password."));
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
}