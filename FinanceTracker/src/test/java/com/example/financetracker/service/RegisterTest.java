package com.example.financetracker.service;

import com.example.financetracker.authentication.AuthenticationService;
import com.example.financetracker.model.pojo.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@SpringBootTest
class RegisterTest {

    @MockBean
    private EmailService emailService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void testVerificationEmail(){
        String expectedEmail = "example@gmail.com";
        String testUrl = "exampleUrl.com";
        String token = "Token";
        String verifyURL =  testUrl + "/verify?code=" + token;
        String content = "Dear user,<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\""
                + verifyURL
                +"\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Finance Tracker team.";

        User user = new User();
        user.setVerificationToken(token);
        user.setEmail(expectedEmail);
        authenticationService.sendVerificationEmail(user, testUrl);
        verify(emailService, times(1))
                .sendEmail("Activate your account.", expectedEmail,
                        content, true, null, null);
    }

}