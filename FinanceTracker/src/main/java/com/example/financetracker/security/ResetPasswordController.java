package com.example.financetracker.security;

import com.example.financetracker.exceptions.BadRequestException;
import com.example.financetracker.exceptions.NotFoundException;
import com.example.financetracker.model.pojo.User;
import com.example.financetracker.service.EmailService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ResetPasswordController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ResetPasswordService resetPasswordService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "account/forgotPassword";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);
        try {
            resetPasswordService.updateResetPasswordToken(token, email);
            String resetPasswordLink =
                    request.getRequestURL().toString().replace(request.getServletPath(), "")
                            + "/reset_password?token=" + token;

            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (NotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "account/forgotPassword";
    }



    public void sendEmail(String recipientEmail, String link) {

        emailService.sendEmail("Here's the link to reset your password", recipientEmail,
                "<p>Hello,</p>"
                        + "<p>You have requested to reset your password.</p>"
                        + "<p>Click the link below to change your password:</p>"
                        + "<p><a href=\"" + link + "\">Change my password</a></p>"
                        + "<br>"
                        + "<p>Ignore this email if you do remember your password, "
                        + "or you have not made the request.</p>",
                true,null, null);

    }


    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = resetPasswordService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "redirect:/login";
        }
        return "account/resetPassword";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match.");
        }

        User user = resetPasswordService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "redirect:/login";
        } else {
            resetPasswordService.updatePassword(user, password);

            model.addAttribute("message", "You have successfully changed your password.");
        }

        return "redirect:/login";
    }
}
