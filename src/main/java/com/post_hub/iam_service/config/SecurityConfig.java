package com.post_hub.iam_service.config;

import com.post_hub.iam_service.security.filter.JwtRequestFilter;
import com.post_hub.iam_service.security.handler.AccessRestrictionHandler;
import com.post_hub.iam_service.service.UserService;
import com.post_hub.iam_service.service.model.IamServiceUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final AccessRestrictionHandler accessRestrictionHandler;

    private static final List<String> NOT_SECURED_URLS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh/token"
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(NOT_SECURED_URLS.toArray(new String[0])).permitAll()
//                        .requestMatchers(HttpMethod.GET, "/users/all").hasAllRoles(adminAccessSecurityRoles())
//                        .requestMatchers(HttpMethod.GET, "/posts/all").hasAllRoles(adminAccessSecurityRoles())
                                .requestMatchers(HttpMethod.POST, "/users/create").hasAnyAuthority(
                                        IamServiceUserRole.SUPER_ADMIN.name(),
                                        IamServiceUserRole.ADMIN.name()
                                )

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                        .accessDeniedHandler(accessRestrictionHandler)
                )
                .addFilterBefore(
                        jwtRequestFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserService userService
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private String[] adminAccessSecurityRoles() {
        return new String[] {
                IamServiceUserRole.SUPER_ADMIN.name(),
                IamServiceUserRole.ADMIN.name()
        };
    }

}
