package com.theodoro.loginservice.api.rest.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.theodoro.loginservice.domains.entities.Role;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.ZonedDateTime;

@JsonPropertyOrder({
        "id",
        "code",
        "description",
        "creationDate",
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(value = "role", collectionRelation = "roles")
public class RoleResponse extends RepresentationModel<RoleResponse> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("creationDate")
    private ZonedDateTime creationDate;

    public RoleResponse(Role role) {
        this.id = role.getId();
        this.code = role.getCode();
        this.description = role.getDescription();
        this.creationDate = role.getCreationDate();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }
}