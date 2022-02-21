package com.example.financetracker.security;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dto.userDTOs.UserRegisterFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping("/register")
    public String register(final Model model){
        model.addAttribute("userRegisterFormDTO", new UserRegisterFormDTO());
        return "account/register";
    }

    @PostMapping("/register")
    public String userRegistration(final @Valid UserRegisterFormDTO userData, final BindingResult bindingResult, final Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationForm", userData);
            return "account/register";
        }
        try {
            authenticationService.register(userData);
        }catch (BadRequestException e){
            bindingResult.rejectValue("email", "userData.email","An account already exists for this email.");
            model.addAttribute("registrationForm", userData);
            return "account/register";
        }
        model.addAttribute("registrationMsg", messageSource.getMessage("user.registration.verification.email.msg", null, LocaleContextHolder.getLocale()));
        return "account/register";
    }

    @GetMapping("/login")
    public String login(){
        return "account/login";
    }

}
