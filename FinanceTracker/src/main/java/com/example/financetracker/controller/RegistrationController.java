package com.example.financetracker.controller;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.model.dto.userDTOs.UserRegisterForm;
import com.example.financetracker.service.UserService;
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
public class RegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping("/register")
    public String register(final Model model){
        model.addAttribute("userRegisterForm", new UserRegisterForm());
        return "account/register";
    }

    @PostMapping("/register")
    public String userRegistration(final @Valid UserRegisterForm userData, final BindingResult bindingResult, final Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationForm", userData);
            return "account/register";
        }
        try {
            userService.register(userData);
        }catch (BadRequestException e){
            bindingResult.rejectValue("email", "userData.email","An account already exists for this email.");
            model.addAttribute("registrationForm", userData);
            return "account/register";
        }
        model.addAttribute("registrationMsg", messageSource.getMessage("user.registration.verification.email.msg", null, LocaleContextHolder.getLocale()));
        return "account/register";
    }


}
