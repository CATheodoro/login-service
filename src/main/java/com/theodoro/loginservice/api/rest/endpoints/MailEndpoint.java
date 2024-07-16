package com.theodoro.loginservice.api.rest.endpoints;

import com.theodoro.loginservice.api.rest.models.requests.MailRequest;
import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.domains.exceptions.NotFoundException;
import com.theodoro.loginservice.domains.services.MailService;
import com.theodoro.loginservice.domains.services.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.ACCOUNT_EMAIL_NOT_FOUND;

@RestController
public class MailEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(MailEndpoint.class);

    public static final String MAIL_RESOURCE_PATH = "/api/mail";
    public static final String MAIL_ACTIVATE_ACCOUNT_PATH = MAIL_RESOURCE_PATH + "/activate-account";
    public static final String MAIL_SEND_TOKEN_EMAIL_PATH = MAIL_RESOURCE_PATH + "/send-token-email";

    private final MailService mailService;
    private final UserAccountService userAccountService;

    @Autowired
    public MailEndpoint(MailService mailService, UserAccountService userAccountService) {
        this.mailService = mailService;
        this.userAccountService = userAccountService;
    }

    @Operation(summary = "Activate the user account via email")
    @GetMapping(MAIL_ACTIVATE_ACCOUNT_PATH)
    public void activateAccount(@RequestParam String token) throws MessagingException {
        mailService.activateAccount(token);
    }

    @Operation(summary = "Resend the activation token to Email")
    @PostMapping(MAIL_SEND_TOKEN_EMAIL_PATH)
    public void revalidationEmail(@RequestBody @Valid MailRequest emailRequest) throws MessagingException {
        UserAccount userAccount = userAccountService.findByEmail(emailRequest.getEmail()).orElseThrow(() -> {
            logger.info("User account not found for email {}.", emailRequest.getEmail());
            return new NotFoundException(ACCOUNT_EMAIL_NOT_FOUND);
        });

        mailService.resendActivationEmail(userAccount);
    }
}
