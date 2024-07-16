package com.theodoro.loginservice.domains.repositories;

import com.theodoro.loginservice.domains.entities.MailToken;
import com.theodoro.loginservice.domains.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailTokenRepository extends JpaRepository<MailToken, String> {
    Optional<MailToken> findByEmailToken(String emailToken);

    Optional<MailToken> findTopByUserAccountOrderByExpiresAtDesc(UserAccount userAccount);
}
