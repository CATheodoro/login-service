package com.theodoro.loginservice.api.rest.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequest {

    @Email(message = "Email is not well formatted")
    @NotBlank(message = "Email is mandatory")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @JsonProperty("password")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}