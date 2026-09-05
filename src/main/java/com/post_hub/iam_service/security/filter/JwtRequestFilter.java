package com.post_hub.iam_service.security.filter;

import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOGIN_PATH = "/auth/login";
    private static final String REGISTER_PATH = "/auth/register";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> authHeader = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));
        String requestURI = request.getRequestURI();

        if (authHeader.isPresent() && authHeader.get().startsWith(BEARER_PREFIX)) {
            String jwt = authHeader.get().substring(BEARER_PREFIX.length());

            try {
                if (!jwtTokenProvider.validateToken(jwt)) {
                    throw new ExpiredJwtException(null, null, ApiErrorMessage.TOKEN_EXPIRED.getMessage());
                }

                String email = jwtTokenProvider.getEmail(jwt);
                String userId = jwtTokenProvider.getUserId(jwt);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null && userId != null) {
                    List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(jwt).stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(email, jwt, authorities);

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // главное исправление — без этой строки аутентификация не работала вообще
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (ExpiredJwtException e) {
                handleTokenExpiration(requestURI, jwt, response);
                return;
            } catch (SignatureException | MalformedJwtException e) {
                handleSignatureException(response);
                return;
            } catch (Exception exception) {
                handleUnexpectedException(response, exception);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void handleTokenExpiration(String requestURI, String jwt, HttpServletResponse response) throws IOException {
        if (isAuthEndpoint(requestURI)) {
            String refreshedToken = jwtTokenProvider.refreshToken(jwt);
            response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + refreshedToken);
        } else {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiErrorMessage.TOKEN_EXPIRED.getMessage());
        }
    }

    private void handleSignatureException(
            HttpServletResponse response
    ) throws IOException {

        sendErrorResponse(
                response,
                HttpStatus.UNAUTHORIZED,
                ApiErrorMessage.INVALID_TOKEN_SIGNATURE.getMessage()
        );
    }

    private void handleUnexpectedException(HttpServletResponse response, Exception exception) throws IOException {
        log.error(ApiErrorMessage.ERROR_DURING_JWT_PROCESSING.getMessage(), exception);
        sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorMessage.UNEXPECTED_ERROR_OCCURRED.getMessage());
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String message
    ) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("message", message);

        objectMapper.writeValue(response.getWriter(), body);
    }

    private boolean isAuthEndpoint(String requestURI) {
        return requestURI.equals(LOGIN_PATH) || requestURI.equals(REGISTER_PATH);
    }
}