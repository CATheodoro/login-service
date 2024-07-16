package com.theodoro.loginservice.api.rest.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.theodoro.loginservice.domains.entities.Role;
import com.theodoro.loginservice.domains.entities.UserAccount;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@JsonPropertyOrder({
        "id",
        "name",
        "email",
        "roles"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(value = "UserAccount", collectionRelation = "UserAccounts")
public class UserAccountResponse extends RepresentationModel<UserAccountResponse> {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("roles")
    private List<Role> roles;

    public UserAccountResponse(UserAccount userAccount) {
        this.id = userAccount.getId();
        this.name = userAccount.getName();
        this.email = userAccount.getUsername();
        this.roles = userAccount.getRoles();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
