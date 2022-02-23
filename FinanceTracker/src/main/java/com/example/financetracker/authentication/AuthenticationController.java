package com.example.financetracker.authentication;

import com.example.financetracker.exceptions.EmailAlreadyExistsException;
import com.example.financetracker.exceptions.PasswordMismatchException;
import com.example.financetracker.model.dto.userDTOs.UserRegisterFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping("/")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String register(final Model model){
        model.addAttribute("userRegisterFormDTO", new UserRegisterFormDTO());
        return "account/register";
    }

    @PostMapping("/register")
    public String userRegistration(final @Valid UserRegisterFormDTO userData, final BindingResult bindingResult, final Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationForm", userData);
            return "account/register";
        }
        try {
            String url = getSiteURL(request);
            authenticationService.register(userData, url);
        } catch (EmailAlreadyExistsException e){
            bindingResult.rejectValue("email", "userData.email");
            model.addAttribute("registrationForm", userData);
            return "account/register";
        } catch (PasswordMismatchException e){
            bindingResult.rejectValue("confirmPassword", "userData.confirmPassword");
            model.addAttribute("registrationForm", userData);
            return "account/register";
        }
        model.addAttribute("registrationMsg", messageSource.getMessage("user.registration.verification.email.msg", null, LocaleContextHolder.getLocale()));
        return "account/registerSuccess";
    }

    @GetMapping("/login")
    public String login(){
        return "account/login";
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (authenticationService.verify(code)) {
            return "account/verifySuccess";
        } else {
            return "account/verifyFail";
        }
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
