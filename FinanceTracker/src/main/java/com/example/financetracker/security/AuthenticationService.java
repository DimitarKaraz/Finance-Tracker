package com.example.financetracker.security;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dto.userDTOs.UserProfileDTO;
import com.example.financetracker.model.dto.userDTOs.UserRegisterFormDTO;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.service.EmailService;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;

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

    public UserProfileDTO register(UserRegisterFormDTO form, String siteURL) {
        if (userRepository.existsByEmail(form.getEmail())) {
            System.out.println("Email already exists");
            throw new BadRequestException("Email already exists.");
        }
        if (!form.getConfirmPassword().equals(form.getPassword())){
            throw new BadRequestException("Password does not match.");
        }
        form.setPassword(encoder.encode(form.getPassword()));
        User user = modelMapper.map(form, User.class);
        user.setLastLogin(LocalDateTime.now());
        user.setVerificationToken(RandomString.make(30));
        user.setEnabled(false);
        user.setAuthorities("ROLE_USER");
        userRepository.save(user);
        sendVerificationEmail(user, siteURL);
        return modelMapper.map(user, UserProfileDTO.class);
    }

    private void sendVerificationEmail(User user, String siteURL) {
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationToken();
        String content = "Dear user,<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\""
                + verifyURL
                +"\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";

        emailService.sendEmail("Activate your account.", user.getEmail(),
                content, false, null, null);
    }

    public boolean verify(String verificationToken) {
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
