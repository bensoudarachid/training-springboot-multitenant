package com.royasoftware.school.settings.validators;

import org.springframework.beans.factory.annotation.Autowired;

import com.royasoftware.school.repository.AccountRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator of unique username
 */
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {

    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        // If the repository is null then return null
        if(accountRepository == null){
            return true;
        }
        // Check if the username is unique
        return accountRepository.findByUsername(username) == null;
    }
}
