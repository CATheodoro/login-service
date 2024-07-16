package com.theodoro.loginservice.api.rest.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theodoro.loginservice.api.rest.validators.annotations.PasswordValidation;
import jakarta.validation.constraints.NotBlank;

public class PasswordChangeRequest {

    @NotBlank(message = "Current password is mandatory")
    @JsonProperty("currentPassword")
    private String currentPassword;

    @NotBlank(message = "New password is mandatory")
    @PasswordValidation
    @JsonProperty("newPassword")
    private String newPassword;

    @NotBlank(message = "Confirm new password is mandatory")
    @JsonProperty("confirmPassword")
    private String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
