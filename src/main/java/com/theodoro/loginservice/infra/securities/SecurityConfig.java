package com.theodoro.loginservice.infra.securities;

import com.theodoro.loginservice.api.rest.endpoints.RoleEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.theodoro.loginservice.api.rest.endpoints.AuthenticationEndpoint.AUTHENTICATION_REFRESH_TOKEN_PATH;
import static com.theodoro.loginservice.api.rest.endpoints.AuthenticationEndpoint.AUTHENTICATION_RESOURCE_PATH;
import static com.theodoro.loginservice.api.rest.endpoints.MailEndpoint.MAIL_ACTIVATE_ACCOUNT_PATH;
import static com.theodoro.loginservice.api.rest.endpoints.MailEndpoint.MAIL_SEND_TOKEN_EMAIL_PATH;
import static com.theodoro.loginservice.api.rest.endpoints.RoleEndpoint.ROLE_RESOURCE_PATH;
import static com.theodoro.loginservice.api.rest.endpoints.UserAccountEndpoint.*;
import static com.theodoro.loginservice.domains.enumerations.RoleEnum.ADMIN;
import static com.theodoro.loginservice.domains.enumerations.RoleEnum.USER;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    private static final String[] WHITELIST = {
            USER_ACCOUNT_RESOURCE_PATH,
            AUTHENTICATION_RESOURCE_PATH,
            AUTHENTICATION_REFRESH_TOKEN_PATH,
            MAIL_ACTIVATE_ACCOUNT_PATH,
            MAIL_SEND_TOKEN_EMAIL_PATH,
    };

    private static final String[] SWAGGER_WHITELIST = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST).permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(USER_ACCOUNT_CHANGE_PASSWORD_PATH).hasAnyAuthority(USER.name(), ADMIN.name())
                        .requestMatchers(USER_ACCOUNT_CHANGE_ROLE_PATH).hasAnyAuthority(ADMIN.name())
                        .requestMatchers(ROLE_RESOURCE_PATH).hasAuthority(ADMIN.name())
                        .requestMatchers(RoleEndpoint.ROLE_SELF_PATH).hasAuthority(ADMIN.name())
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}