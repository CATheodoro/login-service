package com.theodoro.loginservice.api.rest.endpoints;

import com.theodoro.loginservice.api.rest.assemblers.UserAccountAssembler;
import com.theodoro.loginservice.api.rest.models.requests.PasswordChangeRequest;
import com.theodoro.loginservice.api.rest.models.requests.RoleChangeRequest;
import com.theodoro.loginservice.api.rest.models.requests.UserAccountRequest;
import com.theodoro.loginservice.api.rest.models.responses.UserAccountResponse;
import com.theodoro.loginservice.domains.entities.Role;
import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.domains.exceptions.BadRequestException;
import com.theodoro.loginservice.domains.exceptions.ConflictException;
import com.theodoro.loginservice.domains.exceptions.NotFoundException;
import com.theodoro.loginservice.domains.services.RoleService;
import com.theodoro.loginservice.domains.services.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.*;
import static com.theodoro.loginservice.domains.enumerations.RoleEnum.USER;

@RestController
public class UserAccountEndpoint {

    static final Logger logger = LoggerFactory.getLogger(UserAccountEndpoint.class);

    public static final String USER_ACCOUNT_RESOURCE_PATH = "/api/user-account";
    public static final String USER_ACCOUNT_SELF_PATH = USER_ACCOUNT_RESOURCE_PATH + "/{id}";
    public static final String USER_ACCOUNT_CHANGE_PASSWORD_PATH = USER_ACCOUNT_SELF_PATH + "/change-password";
    public static final String USER_ACCOUNT_CHANGE_ROLE_PATH = USER_ACCOUNT_SELF_PATH + "/role";

    private final UserAccountService userAccountService;
    private final UserAccountAssembler userAccountAssembler;
    private final RoleService roleService;

    @Autowired
    public UserAccountEndpoint(UserAccountService userAccountService,
                               UserAccountAssembler userAccountAssembler,
                               RoleService roleService) {
        this.userAccountService = userAccountService;
        this.userAccountAssembler = userAccountAssembler;
        this.roleService = roleService;
    }

    @ApiResponse(responseCode = "201", description = "Successfully created")
    @ApiResponse(responseCode = "404", description = "Role was not initialized.", content = @Content)
    @ApiResponse(responseCode = "409", description = "E-mail already registered.", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "User register endpoint")
    @PostMapping(USER_ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<URI> register(@RequestBody @Valid UserAccountRequest request) throws MessagingException {
        Role role = roleService.findByCode(USER.name()).orElseThrow(() ->
                new NotFoundException(ROLE_NOT_INITIALIZED_NOT_FOUND));

        userAccountService.findByEmail(request.getEmail()).ifPresent(search -> {
            throw new ConflictException(USER_ALREADY_EXISTS,
                    userAccountAssembler.buildSelfLink(search.getId()).toUri());
        });
        UserAccount userAccount = userAccountService.register(request, role);
        return ResponseEntity.created(userAccountAssembler.buildSelfLink(userAccount.getId()).toUri()).build();
    }


    @Operation(summary = "Returns all registered users")
    @GetMapping(USER_ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<List<UserAccountResponse>> findAll(){

        List<UserAccount> accounts = userAccountService.findAll();
        return ResponseEntity.ok(userAccountAssembler.toListModel(accounts));
    }

    @ApiResponse(responseCode = "200", description = "Consultation carried out successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "404", description = "User account not found for id informed.", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "User search endpoint by id")
    @GetMapping(USER_ACCOUNT_SELF_PATH)
    public ResponseEntity<UserAccountResponse> findById(@PathVariable("id") final String id) {

        final UserAccount userAccount = userAccountService.findById(id).orElseThrow(() -> {
            logger.info("User account not found for id: {}", id);
            return new NotFoundException(ACCOUNT_ID_NOT_FOUND);
        });
        return ResponseEntity.ok(userAccountAssembler.toModel(userAccount));
    }

    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    @ApiResponse(responseCode = "400", description = "Confirmation password and new password are not the same.", content = @Content)
    @ApiResponse(responseCode = "400", description = "New password and current password are the same.", content = @Content)
    @ApiResponse(responseCode = "400", description = "Current password is incorrect.", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "403", description = "Not authorized.", content = @Content)
    @ApiResponse(responseCode = "404", description = "User account not found for id informed.", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "Change password endpoint")
    @PatchMapping(USER_ACCOUNT_CHANGE_PASSWORD_PATH)
    public ResponseEntity<UserAccountResponse> changePassword(@PathVariable("id") final String id, @RequestBody @Valid PasswordChangeRequest request) {
        UserAccount userAccount = userAccountService.changePassword(id ,request);
        return ResponseEntity.ok().location(userAccountAssembler.buildSelfLink(userAccount.getId()).toUri()).build();
    }

    @ApiResponse(responseCode = "200", description = "Password updated successfully", content = @Content)
    @ApiResponse(responseCode = "404", description = "User account not found for id informed.", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "Add role endpoint")
    @PatchMapping(USER_ACCOUNT_CHANGE_ROLE_PATH)
    public ResponseEntity<URI> addUserRole(@PathVariable("id") final String id, @RequestBody @Valid RoleChangeRequest request) {

        UserAccount userAccount = userAccountService.findById(id).orElseThrow(() -> {
            logger.info("User account not found for id: {}", id);
            return new NotFoundException(ACCOUNT_ID_NOT_FOUND);
        });
        Set<String> existingRoleCodes = userAccount.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());

        List<Role> roles = request.getCode().stream().map(code -> {
            if (existingRoleCodes.contains(code)) {
                throw new ConflictException(USER_ALREADY_HAVE_ROLE);
            }

            return roleService.findByCode(code).orElseThrow(() -> {
                logger.error("Role not found for code: {}", code);
                return new NotFoundException(ROLE_ID_NOT_FOUND);
            });
        }).toList();
        userAccount.getRoles().addAll(roles);
        userAccount = userAccountService.save(userAccount);
        return ResponseEntity.ok().location(userAccountAssembler.buildSelfLink(userAccount.getId()).toUri()).build();
    }

    @ApiResponse(responseCode = "200", description = "Password updated successfully", content = @Content)
    @ApiResponse(responseCode = "404", description = "User not found for Id informed", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)

    @Operation(summary = "Change or add role endpoint")
    @DeleteMapping(USER_ACCOUNT_CHANGE_ROLE_PATH)
    public ResponseEntity<URI> removeUserRole(@PathVariable("id") final String id, @RequestBody @Valid RoleChangeRequest request) {

        UserAccount userAccount = userAccountService.findById(id).orElseThrow(() -> {
            logger.info("User account not found for id: {}", id);
            return new NotFoundException(ACCOUNT_ID_NOT_FOUND);
        });

        request.getCode().stream().filter(code -> roleService.findByCode(code).isEmpty())
                .findFirst().ifPresent(code -> {
                    logger.error("Role not found for code: {}", code);
                    throw new NotFoundException(ROLE_ID_NOT_FOUND);
                });

        List<Role> rolesToRemove = userAccount.getRoles().stream()
                .filter(role -> request.getCode().contains(role.getCode()))
                .toList();

        if (rolesToRemove.isEmpty()) {
            logger.error("User id: {} does not have this rule", userAccount.getId());
            throw new BadRequestException(USER_NOT_HAVE_ROLE_BAD_REQUEST);
        }
        userAccount.getRoles().removeAll(rolesToRemove);
        userAccount = userAccountService.save(userAccount);
        return ResponseEntity.ok().location(userAccountAssembler.buildSelfLink(userAccount.getId()).toUri()).build();
    }
}
