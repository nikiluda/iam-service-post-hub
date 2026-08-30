package com.post_hub.iam_service.controller;



import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.constants.ApiLogMessage;
import com.post_hub.iam_service.model.dto.user.UserDTO;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.request.user.UpdateUserRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.service.UserService;
import com.post_hub.iam_service.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("${end-points.users}")
@Validated
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("${end-points.id}")
    public ResponseEntity<IamResponse<UserDTO>> getUserById(@PathVariable(name = "id") Integer userId) {

        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        IamResponse<UserDTO> response = userService.getByID(userId);

        return ResponseEntity.ok(response);

    }


    @PostMapping("${end-points.create}")
    public ResponseEntity<IamResponse<UserDTO>> createUser(
            @RequestBody @Valid NewUserRequest newUserRequest
            ) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserDTO> response = userService.createUser(newUserRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("${end-points.id}")
    public ResponseEntity<IamResponse<UserDTO>> updateUserById(
            @PathVariable(name = "id") Integer userId,
            @RequestBody @Valid UpdateUserRequest updateUserRequest) {

        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserDTO> response = userService.updateUser(userId,updateUserRequest);
        return ResponseEntity.ok(response);

    }


    @DeleteMapping("${end-points.id}")
    public ResponseEntity<Void> deleteById(@PathVariable(name = "id") Integer userID) {

        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        userService.softDeleteUser(userID);

        return ResponseEntity.noContent().build();

    }
}
