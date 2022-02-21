package com.example.financetracker.security;

import com.example.financetracker.exceptions.UnauthorizedException;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        User user = userRepository.findById(MyUserDetailsService.getCurrentUserId())
                .orElseThrow(() -> {throw new UnauthorizedException("Invalid user id.");});
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        this.setDefaultTargetUrl("/profile");

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
