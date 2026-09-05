package com.post_hub.iam_service.security.validation;

import com.post_hub.iam_service.model.dto.user.RegistrationUserRequest;
import com.post_hub.iam_service.utils.PasswordMatchers;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatchers, RegistrationUserRequest> {
    @Override
    public boolean isValid(RegistrationUserRequest request, ConstraintValidatorContext constraintValidatorContext) {
        return request.getPassword().equals(request.getConfirmPassword());
    }
}
