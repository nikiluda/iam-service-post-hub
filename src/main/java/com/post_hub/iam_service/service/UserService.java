package com.post_hub.iam_service.service;

import com.post_hub.iam_service.model.dto.user.UserDTO;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.request.user.UpdateUserRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import jakarta.validation.constraints.NotNull;

public interface UserService {

    IamResponse<UserDTO> getByID(@NotNull Integer userId);

    IamResponse<UserDTO> createUser(@NotNull NewUserRequest newUserRequest);

    IamResponse<UserDTO> updateUser(@NotNull Integer userId, @NotNull UpdateUserRequest updateUserRequest);
}
