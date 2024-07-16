package com.theodoro.loginservice.domains.services;

import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.domains.exceptions.BadRequestException;
import com.theodoro.loginservice.domains.exceptions.NotFoundException;
import com.theodoro.loginservice.domains.repositories.UserAccountRepository;
import com.theodoro.loginservice.infra.securities.JwtService;
import com.theodoro.loginservice.api.rest.models.requests.AuthenticationRequest;
import com.theodoro.loginservice.api.rest.models.responses.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.ACCOUNT_EMAIL_NOT_FOUND;
import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.USER_NOT_AUTHENTICATED_BAD_REQUEST;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class AuthenticationService {
    static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserAccountRepository userAccountRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userAccountRepository = userAccountRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        logger.info("Starting authentication for email: {}", request.getEmail());
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserAccount userAccount = ((UserAccount)auth.getPrincipal());
        logger.info("Authentication successful for email: {}", request.getEmail());

        return new AuthenticationResponse(jwtService.generateToken(userAccount), jwtService.generateRefreshToken(userAccount));
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new BadRequestException(USER_NOT_AUTHENTICATED_BAD_REQUEST);
        }
        final String refreshToken = authHeader.substring(7);
        final String accountEmail = jwtService.extractUsername(refreshToken);
        if (accountEmail != null){
            UserAccount userAccount = userAccountRepository.findByEmail(accountEmail).orElseThrow(() -> {
                logger.info("User credential not found for email informed.");
                return new NotFoundException(ACCOUNT_EMAIL_NOT_FOUND);
            });
            if (jwtService.isTokenValid(refreshToken, userAccount)) {

                String accessToken = jwtService.generateToken(userAccount);

                return new AuthenticationResponse(accessToken, refreshToken);
            }
        }
        throw new NotFoundException(ACCOUNT_EMAIL_NOT_FOUND);
    }
}
