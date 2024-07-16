package com.theodoro.loginservice.domains.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MAIL_TOKEN")
public class MailToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "MAIL_TOKEN", unique = true)
    private String emailToken;

    @Column(name = "CREATE_AT")
    private LocalDateTime createdAt;

    @Column(name = "EXPIRES_AT")
    private LocalDateTime expiresAt;

    @Column(name = "VALIDATED_AT")
    private LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name = "ID_USER_ACCOUNT", nullable = false)
    private UserAccount userAccount;

    public MailToken() {
    }

    public MailToken(String emailToken, LocalDateTime createdAt, LocalDateTime expiresAt, UserAccount userAccount) {
        this.emailToken = emailToken;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.userAccount = userAccount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String mailToken) {
        this.emailToken = mailToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
