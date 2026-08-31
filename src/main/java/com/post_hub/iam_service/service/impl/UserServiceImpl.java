package com.post_hub.iam_service.service.impl;


import com.post_hub.iam_service.mapper.UserMapper;
import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.dto.user.UserDTO;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.DataExistsException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.request.user.UpdateUserRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.service.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public IamResponse<UserDTO> getByID(@NotNull Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY.getMessage(userId)));

        UserDTO userDTO = userMapper.toDto(user);

        return IamResponse.createSuccessful(userDTO);


    }

    @Override
    public IamResponse<UserDTO> createUser(@NotNull NewUserRequest newUserRequest) {
        if (userRepository.existsByUsername(newUserRequest.getUsername())) {
            throw new DataExistsException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(newUserRequest.getUsername()));
        }

        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new DataExistsException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(newUserRequest.getEmail()));
        }

        User user = userMapper.createUser(newUserRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO userDTO = userMapper.toDto(savedUser);
        return IamResponse.createSuccessful(userDTO);
    }

    @Override
    public IamResponse<UserDTO> updateUser(@NotNull Integer userId, @NotNull UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY.getMessage(userId)));
        userMapper.updateUser(user, updateUserRequest);
        user.setUpdated(LocalDateTime.now());
        userRepository.save(user);

        UserDTO userDTO = userMapper.toDto(user);
        return IamResponse.createSuccessful(userDTO);

    }

    @Override
    public void softDeleteUser(@NotNull Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY.getMessage(userId)));

        user.setDeleted(true);
        userRepository.save(user);
    }

}
