package com.theodoro.loginservice.domains.services;

import com.theodoro.loginservice.domains.entities.MailToken;
import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.domains.enumerations.MailTemplateName;
import com.theodoro.loginservice.domains.exceptions.BadRequestException;
import com.theodoro.loginservice.domains.exceptions.NotFoundException;
import com.theodoro.loginservice.domains.repositories.UserAccountRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final UserAccountRepository userAccountRepository;
    private final MailTokenService mailTokenService;

    @Autowired
    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, UserAccountRepository userAccountRepository, MailTokenService mailTokenService) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.userAccountRepository = userAccountRepository;
        this.mailTokenService = mailTokenService;
    }

    @Value("${mail.username}")
    private String emailUsername;

    @Value("${mail.frontend.activation-url}")
    private String activationUrl;

    public void activateAccount(String emailToken) throws MessagingException {
        MailToken savedToken = mailTokenService.findByEmailToken(emailToken).orElseThrow(()-> {
            logger.error("Token not found: {}", emailToken);
            return new NotFoundException(TOKEN_NOT_FOUND);
        });

        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendActivationEmail(savedToken.getUserAccount());
            throw new BadRequestException(TOKEN_EXPIRED);
        }

        UserAccount userAccount = userAccountRepository.findById(savedToken.getUserAccount().getId()).orElseThrow(() -> {
            logger.error("UserAccount not found for ID: {}", savedToken.getUserAccount().getId());
            return new NotFoundException(ACCOUNT_ID_NOT_FOUND);
        });

        if(userAccount.isEnabled()) {
            logger.info("UserAccount already activated");
            throw new BadRequestException(ACCOUNT_AlREADY_ACTIVATE_BAD_REQUEST);
        }

        activateAccountAndSaveToken(userAccount, savedToken);
    }

    private void activateAccountAndSaveToken(UserAccount userAccount, MailToken emailToken) {
        userAccount.setEnabled(true);
        userAccountRepository.save(userAccount);

        emailToken.setValidatedAt(LocalDateTime.now());
        mailTokenService.save(emailToken);

        logger.info("UserAccount {} activated successfully", userAccount.getEmail());
    }

    @Async
    public void resendActivationEmail(UserAccount userAccount) throws MessagingException {
        MailToken emailToken = mailTokenService.findTopByUserAccountOrderByExpiresAtDesc(userAccount).orElseThrow(() -> new NotFoundException(TOKEN_NOT_FOUND));

        if (emailToken.getValidatedAt() != null) {
            logger.info("UserAccount already activated");
            throw new BadRequestException(ACCOUNT_AlREADY_ACTIVATE_BAD_REQUEST);
        }

        if (LocalDateTime.now().isBefore(emailToken.getCreatedAt().plusMinutes(1))){
            throw new BadRequestException(TOKEN_CAN_NOT_RESEND);
        }

        logger.warn("Resending Token for userAccount: {}. Sending new email Token email.", emailToken.getUserAccount().getEmail());
        sendActivationEmail(userAccount);
    }

    public void sendActivationEmail(UserAccount userAccount) throws MessagingException {
        String newToken = mailTokenService.saveTokenUserAccount(userAccount);
        this.sendEmail(
                userAccount.getEmail(),
                userAccount.getName(),
                MailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "UserAccount activation"
        );

        logger.info("New activation email Token sent to {}", userAccount.getEmail());
    }

    public void sendEmail(String to, String email, MailTemplateName emailTemplate, String confirmationUrl, String activationCode, String subject) throws MessagingException {
        String templateName;
        if(emailTemplate == null) {
            templateName = "confirm-email";
        } else {
            templateName = emailTemplate.name();
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", email);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailUsername);
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);

        helper.setText(template, true);

        javaMailSender.send(mimeMessage);
    }
}