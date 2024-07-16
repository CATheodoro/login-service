package com.theodoro.loginservice.domains.services;

import com.theodoro.loginservice.domains.entities.MailToken;
import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.domains.repositories.MailTokenRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MailTokenService {
    private final MailTokenRepository mailTokenRepository;

    public MailTokenService(MailTokenRepository mailTokenRepository) {
        this.mailTokenRepository = mailTokenRepository;
    }

    public Optional<MailToken> findById(String id) {
        return mailTokenRepository.findById(id);
    }

    public Optional<MailToken> findByEmailToken(String id) {
        return mailTokenRepository.findByEmailToken(id);
    }

    public List<MailToken> findAll() {
        return mailTokenRepository.findAll();
    }

    public String saveTokenUserAccount(UserAccount userAccount) {
        String generatedToken = generateActivationCode();

        MailToken mailToken = new MailToken(generatedToken, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), userAccount);

        mailTokenRepository.save(mailToken);
        return generatedToken;
    }

    public MailToken save(MailToken mailToken) {
        return mailTokenRepository.save(mailToken);
    }

    public Optional<MailToken> findTopByUserAccountOrderByExpiresAtDesc(UserAccount userAccount) {
        return mailTokenRepository.findTopByUserAccountOrderByExpiresAtDesc(userAccount);
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i< 6; i++){
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}