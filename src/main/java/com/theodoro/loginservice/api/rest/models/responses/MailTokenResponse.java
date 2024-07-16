package com.theodoro.loginservice.api.rest.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.theodoro.loginservice.domains.entities.MailToken;
import com.theodoro.loginservice.domains.entities.UserAccount;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "mailToken",
        "createdAt",
        "expiresAt",
        "validatedAt",
        "userAccount"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(value = "token", collectionRelation = "tokens")
public class MailTokenResponse extends RepresentationModel<MailTokenResponse> {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("MAIL_TOKEN")
    private String mailToken;

    @JsonProperty("CREATED_AT")
    private LocalDateTime createdAt;

    @JsonProperty("EXPIRE_AT")
    private LocalDateTime expiresAt;

    @JsonProperty("VALIDATE_AT")
    private LocalDateTime validatedAt;

    @JsonProperty("USER_ACCOUNT")
    private UserAccount userAccount;

    public MailTokenResponse(MailToken token) {
        this.id = token.getId();
        this.mailToken = token.getEmailToken();
        this.createdAt = token.getCreatedAt();
        this.expiresAt = token.getExpiresAt();
        this.validatedAt = token.getValidatedAt();
        this.userAccount = token.getUserAccount();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String mailToken() {
        return mailToken;
    }

    public void setMailToken(String mailToken) {
        this.mailToken = mailToken;
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

    public UserAccount userAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
