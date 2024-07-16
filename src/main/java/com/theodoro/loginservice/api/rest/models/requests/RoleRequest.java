package com.theodoro.loginservice.api.rest.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class RoleRequest {

    @NotBlank(message = "Code is mandatory")
    @JsonProperty("code")
    private String code;

    @NotBlank(message = "Description is mandatory")
    @JsonProperty("description")
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
