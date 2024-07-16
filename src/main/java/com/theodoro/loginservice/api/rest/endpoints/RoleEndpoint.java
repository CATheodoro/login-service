package com.theodoro.loginservice.api.rest.endpoints;

import com.theodoro.loginservice.domains.entities.Role;
import com.theodoro.loginservice.domains.exceptions.ConflictException;
import com.theodoro.loginservice.domains.exceptions.NotFoundException;
import com.theodoro.loginservice.domains.services.RoleService;
import com.theodoro.loginservice.api.rest.assemblers.RoleAssembler;
import com.theodoro.loginservice.api.rest.models.requests.RoleRequest;
import com.theodoro.loginservice.api.rest.models.responses.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.ROLE_ALREADY_EXISTS;
import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.ROLE_ID_NOT_FOUND;

@Controller
public class RoleEndpoint {
    static final Logger logger = LoggerFactory.getLogger(RoleEndpoint.class);

    public static final String ROLE_RESOURCE_PATH = "/api/roles";
    public static final String ROLE_SELF_PATH = ROLE_RESOURCE_PATH + "/{id}";

    private final RoleService roleService;
    private final RoleAssembler roleAssembler;

    @Autowired
    public RoleEndpoint(RoleService roleService, RoleAssembler roleAssembler) {
        this.roleService = roleService;
        this.roleAssembler = roleAssembler;
    }

    @ApiResponse(responseCode = "200", description = "Consultation carried out successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "Returns all registered rules")
    @GetMapping(ROLE_RESOURCE_PATH)
    public ResponseEntity<List<RoleResponse>> findAll() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roleAssembler.toListModel(roles));
    }

    @ApiResponse(responseCode = "200", description = "Successfully created")
    @ApiResponse(responseCode = "409", description = "Role already registered", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "Register a new Rule in the application")
    @PostMapping(ROLE_RESOURCE_PATH)
    public ResponseEntity<RoleResponse> save(@RequestBody @Valid RoleRequest request) {
        roleService.findByCode(request.getCode()).ifPresent(searchedRule -> {
            logger.info("Role {} already exist.", request.getCode());
            throw new ConflictException(ROLE_ALREADY_EXISTS,
                    roleAssembler.buildSelfLink(searchedRule.getId()).toUri());
        });

        Role role = roleService.save(roleAssembler.toEntity(request));
        return ResponseEntity.created(roleAssembler.buildSelfLink(role.getId()).toUri()).build();
    }


    @ApiResponse(responseCode = "200", description = "Consultation by id carried out successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "Find a Rule by id in the application")
    @GetMapping(ROLE_SELF_PATH)
    public ResponseEntity<RoleResponse> findById(@PathVariable("id") final String id) {
        Role role = roleService.findById(id).orElseThrow(() ->{
            logger.info("Role with ID {} not found.", id);
            return new NotFoundException(ROLE_ID_NOT_FOUND);
        });
        return ResponseEntity.ok().body(roleAssembler.toModel(role));
    }
}