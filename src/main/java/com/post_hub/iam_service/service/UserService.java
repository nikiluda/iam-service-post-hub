package com.post_hub.iam_service.service;


import com.post_hub.iam_service.model.dto.user.UserDTO;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.request.user.UpdateUserRequest;
import com.post_hub.iam_service.model.response.IamResponse;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    IamResponse<UserDTO> getByID(@NotNull Integer userId);

    IamResponse<UserDTO> createUser(@NotNull NewUserRequest newUserRequest);

    IamResponse<UserDTO> updateUser(@NotNull Integer userId, @NotNull UpdateUserRequest updateUserRequest);

    void softDeleteUser(@NotNull Integer userId);

}
