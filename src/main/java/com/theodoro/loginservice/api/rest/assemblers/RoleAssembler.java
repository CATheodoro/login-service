package com.theodoro.loginservice.api.rest.assemblers;

import com.theodoro.loginservice.api.rest.endpoints.RoleEndpoint;
import com.theodoro.loginservice.api.rest.models.requests.RoleRequest;
import com.theodoro.loginservice.domains.entities.Role;
import com.theodoro.loginservice.api.rest.models.responses.RoleResponse;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoleAssembler extends RepresentationModelAssemblerSupport<Role, RoleResponse> {

    public RoleAssembler() {
        super(RoleEndpoint.class, RoleResponse.class);
    }

    @Override
    @NonNull
    public RoleResponse toModel(@NonNull Role role) {
        RoleResponse response = new RoleResponse(role);
        response.add(this.buildSelfLink(role.getId()));
        return response;
    }

    public Link buildSelfLink(String id) {
        return linkTo(methodOn(RoleEndpoint.class).findById(id)).withSelfRel();
    }

    public Role toEntity(RoleRequest request) {
        Role role = new Role();
        role.setCode(request.getCode().toUpperCase());
        role.setDescription(request.getDescription());
        return role;
    }

    public List<RoleResponse> toListModel(List<Role> roles) {
        return roles.stream()
                .map(this::toModel)
                .toList();
    }
}