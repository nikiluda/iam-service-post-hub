package com.post_hub.iam_service.controller;



import com.post_hub.iam_service.model.constants.ApiLogMessage;
import com.post_hub.iam_service.model.dto.user.LoginRequest;
import com.post_hub.iam_service.model.dto.user.RegistrationUserRequest;
import com.post_hub.iam_service.model.dto.user.UserProfileDTO;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.service.AuthService;
import com.post_hub.iam_service.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${end-points.auth}")
@Tag(name = "Auth", description = "Authorization method")
public class AuthController {

    private final AuthService authService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authorization",
                content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{ \"token\": \"eyDASjnjfnsj...\"}")))
    })
    @PostMapping("${end-points.login}")
    @Operation(
            summary = "User login",
            description = "Authentication the user and returns an access/refresh token"
    )
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse httpServletResponse) {

        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserProfileDTO> result = authService.login(request);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getPayload().getToken());
        httpServletResponse.addCookie(authorizationCookie);
        return ResponseEntity.ok(result);
    }

    @GetMapping("${end-points.refresh.token}")
    @Operation(
            summary = "Refresh access token",
            description = "Generates new access token using provided refresh token"
    )
    public ResponseEntity<IamResponse<UserProfileDTO>> refreshToken(
            @RequestParam(name = "token") String refreshToken,
            HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserProfileDTO> result = authService.refreshAccessToken(refreshToken);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getPayload().getToken());
        response.addCookie(authorizationCookie);

        return ResponseEntity.ok(result);
    }

    @PostMapping("${end-points.register}")
    @Operation(
            summary = "Register a new user",
            description = "Creates new user and returns authentication details"
    )
    public ResponseEntity<?> register(@RequestBody RegistrationUserRequest request,
                                      HttpServletResponse response) {

        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        IamResponse<UserProfileDTO> result = authService.registrationUser(request);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getPayload().getToken());
        response.addCookie(authorizationCookie);
        return ResponseEntity.ok(result);
    }

}
