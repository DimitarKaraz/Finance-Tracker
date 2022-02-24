package com.example.financetracker.authentication;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.EmailAlreadyExistsException;
import com.example.financetracker.exceptions.PasswordMismatchException;
import com.example.financetracker.model.dto.userDTOs.UserRegisterFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        model.addAttribute("registerForm", new UserRegisterFormDTO());
        return "account/register";
    }

    @PostMapping("/register")
    public String userRegistration(final @Valid @ModelAttribute("registerForm") UserRegisterFormDTO userData, final BindingResult bindingResult, final Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerForm", userData);
            return "account/register";
        }
        try {
            String url = getSiteURL(request);
            authenticationService.register(userData, url);
        } catch (EmailAlreadyExistsException e){
            bindingResult.rejectValue("email", "userData.email");
            model.addAttribute("registerForm", userData);
            return "account/register";
        } catch (BadRequestException e){
            bindingResult.rejectValue("password", "userData.password");
            model.addAttribute("registerForm", userData);
            return "account/register";
        } catch (PasswordMismatchException e){
            bindingResult.rejectValue("confirmPassword", "userData.confirmPassword");
            model.addAttribute("registerForm", userData);
            return "account/register";
        }
        model.addAttribute("registrationMsg", "Thanks for your registration. " +
                                                                    "We have sent a verification email. " +
                                                                    "Please verify your account.");
        return "account/registerSuccess";
    }

    @GetMapping("/login")
    public String login(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "account/login";
        }
        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(){
        return "account/logout";
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
