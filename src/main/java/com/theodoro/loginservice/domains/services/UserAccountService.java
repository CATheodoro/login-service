package com.theodoro.loginservice.domains.services;

import com.theodoro.loginservice.domains.entities.Role;
import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.domains.exceptions.BadRequestException;
import com.theodoro.loginservice.domains.exceptions.NotFoundException;
import com.theodoro.loginservice.domains.exceptions.UnauthorizedException;
import com.theodoro.loginservice.domains.repositories.UserAccountRepository;
import com.theodoro.loginservice.api.rest.models.requests.PasswordChangeRequest;
import com.theodoro.loginservice.api.rest.models.requests.UserAccountRequest;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.*;

@Service
public class UserAccountService {

    static final Logger logger = LoggerFactory.getLogger(UserAccountService.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public Optional<UserAccount> findByEmail(String email) {
        return this.userAccountRepository.findByEmail(email);
    }

    public List<UserAccount> findAll() {return this.userAccountRepository.findAll();}

    public Optional<UserAccount> findById(String id) {
        return this.userAccountRepository.findById(id);
    }

    public UserAccount register(UserAccountRequest request, Role role) throws MessagingException {
        UserAccount userAccount = new UserAccount(
                List.of(role),
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                ZonedDateTime.now()
        );
        userAccount = this.save(userAccount);
        mailService.sendActivationEmail(userAccount);

        return userAccount;
    }

    public UserAccount save(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    public static UserAccount getCurrentUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserAccount userAccount) {
            logger.debug("Retrieving current user credential: {}", userAccount.getUsername());
            return userAccount;
        }
        logger.info(USER_NOT_AUTHENTICATED_BAD_REQUEST.getMessage());
        throw new BadRequestException(USER_NOT_AUTHENTICATED_BAD_REQUEST);
    }

    public UserAccount changePassword(String id, PasswordChangeRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(WRONG_CONFIRMATION_PASSWORD);
        }

        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new BadRequestException(SAME_CURRENT_AND_NEW_PASSWORD);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException(NOT_AUTHORIZED);
        }

        UserAccount userAccount = getCurrentUserAccount();

        if(!Objects.equals(id, userAccount.getId()) && userAccount.isAdmin()) {
            logger.info("Admin id: {} changing password for user id: {}", userAccount.getId(), id);

            userAccount = this.findById(id).orElseThrow(() -> {
                logger.info("User account not found for id: {}", id);
                return new NotFoundException(ACCOUNT_ID_NOT_FOUND);
            });
        } else {
            if(!passwordEncoder.matches(request.getCurrentPassword(), userAccount.getPassword())){
                throw new BadRequestException(WRONG_PASSWORD);
            }
        }

        logger.info("Changing password for user id: {}", userAccount.getId());
        userAccount.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userAccount.setLastUpdateDatePassword(ZonedDateTime.now());
        return this.save(userAccount);
    }
}
