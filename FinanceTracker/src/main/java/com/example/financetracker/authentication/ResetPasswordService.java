package com.example.financetracker.authentication;

import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.passwordValidators.ValidPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintTarget;

@Service
@Transactional
public class ResetPasswordService {

    @Autowired
    private UserRepository userRepository;

    public void updateResetPasswordToken(String token, String email) throws NotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new NotFoundException("Invalid email");
        }
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @ValidPassword(validationAppliesTo = ConstraintTarget.PARAMETERS)
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }
}