package com.post_hub.iam_service.security.validation;

import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.DataExistsException;
import com.post_hub.iam_service.model.exception.InvalidPasswordException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.service.model.IamServiceUserRole;
import com.post_hub.iam_service.utils.ApiUtils;
import com.post_hub.iam_service.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class AccessValidator {

    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(AccessValidator.class);

    public void validateNewUser(String username, String email, String password, String confirmPassword) {

        userRepository.findByUsername(username).ifPresent(existingUser -> {
            throw new DataExistsException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(username));
        });

        userRepository.findByEmail(email).ifPresent(existingUser -> {
            throw new DataExistsException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(email));
        });

        if (!password.equals(confirmPassword)) {
            throw new InvalidPasswordException(ApiErrorMessage.MISMATCH_PASSWORD.getMessage());
        }

        if (PasswordUtils.isNotValidPassword(password))
            throw new InvalidPasswordException(ApiErrorMessage.INVALID_ID_PASSWORD.getMessage());

    }

    public boolean isAdminOrSuperAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException(
                                ApiErrorMessage.EMAIL_NOT_FOUND.getMessage(email)
                        ));

        return user.getRoles().stream()
                .map(role -> IamServiceUserRole.fromName(role.getName()))
                .anyMatch(role ->
                        role == IamServiceUserRole.ADMIN ||
                                role == IamServiceUserRole.SUPER_ADMIN
                );
    }

    @SneakyThrows
    public void validateAdminOrOwnerAccess(String ownerEmail, String createBy) {
        String currentEmail = ApiUtils.getCurrentUsername();


        log.info("currentEmail = {}", currentEmail);
        log.info("ownerEmail = {}", ownerEmail);
        log.info("createBy = {}", createBy);
        log.info("isAdmin = {}", isAdminOrSuperAdmin(currentEmail));
        if (!currentEmail.equals(ownerEmail)
                && !currentEmail.equals(createBy)
                && !isAdminOrSuperAdmin(currentEmail)) {

            throw new AccessDeniedException(
                    ApiErrorMessage.HAVE_NO_ACCESS.getMessage()
            );
        }
    }
}
