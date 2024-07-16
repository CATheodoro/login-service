package com.theodoro.loginservice.api.rest.assemblers;

import com.theodoro.loginservice.api.rest.models.responses.UserAccountResponse;
import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.api.rest.endpoints.UserAccountEndpoint;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAccountAssembler extends RepresentationModelAssemblerSupport<UserAccount, UserAccountResponse> {

    public UserAccountAssembler() {
        super(UserAccountEndpoint.class, UserAccountResponse.class);
    }

    public Link buildSelfLink(String id) {
        return linkTo(methodOn(UserAccountEndpoint.class).findById(id)).withSelfRel();
    }

    @Override
    @NonNull
    public UserAccountResponse toModel(@NonNull UserAccount entity) {
        final UserAccountResponse response = new UserAccountResponse(entity);
        response.add(this.buildSelfLink(entity.getId()));
        return response;
    }

    public List<UserAccountResponse> toListModel(List<UserAccount> userAccount) {
        return userAccount.stream()
                .map(this::toModel)
                .toList();
    }
}
