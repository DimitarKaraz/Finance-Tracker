package com.example.financetracker.passwordValidators;

import com.example.financetracker.exceptions.BadRequestException;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        Properties props = new Properties();
        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream("passay.properties");
        try {
            props.load(inputStream);
        } catch (IOException e) {
            throw new BadRequestException("Password is too weak.");
        }
        MessageResolver resolver = new PropertiesMessageResolver(props);
        PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
        // length between 8 and 50 characters
        new LengthRule(8, 50),
        // at least one upper-case character
        new CharacterRule(EnglishCharacterData.UpperCase, 1),
        // at least one lower-case character
        new CharacterRule(EnglishCharacterData.LowerCase, 1),
        // at least one digit character
        new CharacterRule(EnglishCharacterData.Digit, 1),
        // at least one symbol (special character)
        new CharacterRule(EnglishCharacterData.Special, 1),
        // no whitespace
        new WhitespaceRule(),
        // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
        // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 12345)
        new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);
        String messageTemplate = String.join("\n", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
