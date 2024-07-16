package com.theodoro.loginservice.api.rest.endpoints;

import com.theodoro.loginservice.api.rest.models.responses.AuthenticationResponse;
import com.theodoro.loginservice.domains.services.AuthenticationService;
import com.theodoro.loginservice.api.rest.models.requests.AuthenticationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationEndpoint {

    public static final String AUTHENTICATION_RESOURCE_PATH = "/api/authenticate";
    public static final String AUTHENTICATION_REFRESH_TOKEN_PATH = AUTHENTICATION_RESOURCE_PATH + "/refresh-token";

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationEndpoint(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @ApiResponse(responseCode = "200", description = "Login successfully")
    @ApiResponse(responseCode = "400", description = "E-mail or Password is incorrect.", content = @Content)
    @ApiResponse(responseCode = "400", description = "Malformed JSON request.", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error.", content = @Content)
    @Operation(summary = "Authenticates in the system by returning a Token")
    @PostMapping(AUTHENTICATION_RESOURCE_PATH)
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authentication(request));
    }

    @ApiResponse(responseCode = "201", description = "Successfully created")
    @ApiResponse(responseCode = "404", description = "User account not found for email informed.", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal application error", content = @Content)
    @Operation(summary = "Generate a new Token")
    @PostMapping(AUTHENTICATION_REFRESH_TOKEN_PATH)
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
