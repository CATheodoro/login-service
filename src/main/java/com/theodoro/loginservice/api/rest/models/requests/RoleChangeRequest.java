package com.theodoro.loginservice.api.rest.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theodoro.loginservice.api.rest.validators.annotations.DuplicatedContentListValidation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RoleChangeRequest {

    @NotNull(message = "Code is mandatory")
    @NotEmpty(message = "Code is mandatory")
    @DuplicatedContentListValidation
    @JsonProperty("code")
    private List<String> code;

    public List<String> getCode() {
        return code;
    }

    public void setCode(List<String> code) {
        this.code = code;
    }
}
