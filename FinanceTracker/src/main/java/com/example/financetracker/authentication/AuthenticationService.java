package com.example.financetracker.authentication;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.EmailAlreadyExistsException;
import com.example.financetracker.exceptions.PasswordMismatchException;
import com.example.financetracker.model.dto.userDTOs.UserRegisterFormDTO;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.service.EmailService;
import net.bytebuddy.utility.RandomString;
import org.assertj.core.util.VisibleForTesting;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EmailService emailService;

    public void register(UserRegisterFormDTO form, String siteURL) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        if (!form.getConfirmPassword().equals(form.getPassword())){
            throw new PasswordMismatchException("Passwords do not match.");
        }
        form.setPassword(encoder.encode(form.getPassword()));
        User user = modelMapper.map(form, User.class);
        user.setLastLogin(LocalDate.now());
        user.setVerificationToken(RandomString.make(30));
        user.setEnabled(false);
        user.setAuthorities("ROLE_USER");
        userRepository.save(user);
        sendVerificationEmail(user, siteURL);
    }

    @VisibleForTesting
    public void sendVerificationEmail(User user, String siteURL) {
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationToken();
        String content = "Dear user,<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\""
                + verifyURL
                +"\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Finance Tracker team.";

        emailService.sendEmail("Activate your account.", user.getEmail(),
                content, true, null, null);
    }

    public boolean verify(String verificationToken) {
        if (verificationToken == null) {
            throw new BadRequestException("Invalid verification token.");
        }
        User user = userRepository.findByVerificationToken(verificationToken);
        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationToken(null);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
    }
}
