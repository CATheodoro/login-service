package com.theodoro.loginservice.endpoints;

import com.theodoro.loginservice.LoginServiceApplicationTests;
import com.theodoro.loginservice.api.rest.models.responses.AuthenticationResponse;
import com.theodoro.loginservice.domains.entities.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static com.theodoro.loginservice.api.rest.endpoints.UserAccountEndpoint.*;
import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.*;
import static com.theodoro.loginservice.domains.enumerations.RoleEnum.ADMIN;
import static com.theodoro.loginservice.domains.enumerations.RoleEnum.USER;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@DisplayName("UserAccountEndpoint Test")
class UserAccountEndpointTest extends LoginServiceApplicationTests<UserAccountEndpointTest> {

    void configureContext(String userId, String name, String email, String password) throws Exception {
        // Get UserAccount
        final String uriGetUser = fromPath(USER_ACCOUNT_SELF_PATH).buildAndExpand(userId).toUriString();

        MvcResult resultGetUser = mockMvc.perform(get(uriGetUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andReturn();

        // Creating objects
        String userContent = resultGetUser.getResponse().getContentAsString();

        UserAccount userAccount = objectMapper.readValue(userContent, UserAccount.class);

        userAccount.setId(userId);
        userAccount.setPassword(password);

        // Configure the security context
        super.securityContext(userAccount);
    }

    @Test
    @DisplayName("Should return created when post user credential register")
    void shouldReturnCreatedWhenPostUserAccountRegister() throws Exception {

        final String uri = fromPath(USER_ACCOUNT_RESOURCE_PATH).toUriString();
        String content = super.getScenarioBody("shouldReturnCreatedWhenPostUserAccountRegister");
        MvcResult result = mockMvc.perform(post(uri)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, containsString(uri)))
                .andReturn();

        mockMvc.perform(get(Objects.requireNonNull(result.getResponse().getHeader(LOCATION))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Carlos T. Damasceno"))
                .andExpect(jsonPath("$.email").value("carlos@gmail.com"))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles.[0].code").value(USER.name()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return conflict when post account register with already email registered")
    void shouldReturnConflictWhenPostAccountRegisterWithAlreadyEmailRegistered() throws Exception {

        final String uri = fromPath(USER_ACCOUNT_RESOURCE_PATH).toUriString();
        String content = super.getScenarioBody("shouldReturnConflictWhenPostAuthenticationRegisterWithAlreadyEmailRegistered");
        mockMvc.perform(post(uri)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(USER_ALREADY_EXISTS.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(USER_ALREADY_EXISTS.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return bad request when post user credential withPassword that not meet requirements")
    void shouldReturnBadRequestWhenPostUserAccountWithPasswordThatNotMeetRequirements() throws Exception {

        final String uri = fromPath(USER_ACCOUNT_RESOURCE_PATH).toUriString();
        String content = super.getScenarioBody("shouldReturnBadRequestWhenPostUserAccountWithPasswordThatNotMeetRequirements");
        mockMvc.perform(post(uri)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.messages", hasSize(4)))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return ok When get account with exist id")
    void shouldReturnOkWhenGetAccountWithExistId() throws Exception {

        final String uri = fromPath(USER_ACCOUNT_SELF_PATH).buildAndExpand("ID_USER_RUAN").toUriString();
        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ID_USER_RUAN"))
                .andExpect(jsonPath("$.name").value("Ruan Felipe"))
                .andExpect(jsonPath("$.email").value("ruan@gmail.com"))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles.[0].code").value(USER.name()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return not found when get account with not found id")
    void shouldReturnNotFoundWhenGetAccountWithNotExistId() throws Exception {

        final String uri = fromPath(USER_ACCOUNT_SELF_PATH).buildAndExpand(-1).toUriString();
        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(ACCOUNT_ID_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(ACCOUNT_ID_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return ok when patch user credential change password")
    void shouldReturnOkWhenPatchUserAccountChangePassword() throws Exception {

        this.configureContext("ID_USER_DUQUE", "Duque de Caixias", "duque@gmail.com", "$2a$10$/Ewn34uMOhPGZFvZtOem9e3Nkf6K/CCkjfMZTBfaeufKXWn7AFRlG");
        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForChangePassword");
        final String uriChangePassword = fromPath(USER_ACCOUNT_CHANGE_PASSWORD_PATH).buildAndExpand("ID_USER_DUQUE").toUriString();
        String contentChangePassword = super.getScenarioBody("shouldReturnOkWhenPatchUserAccountChangePassword");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        MvcResult result = mockMvc.perform(patch(uriChangePassword)
                        .header(AUTHORIZATION, authHeader)
                        .content(contentChangePassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(Objects.requireNonNull(result.getResponse().getHeader(LOCATION))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Duque de Caixias"))
                .andExpect(jsonPath("$.email").value("duque@gmail.com"))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles.[0].code").value(USER.name()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return ok when patch user credential change password by admin")
    void shouldReturnOkWhenPatchUserAccountChangePasswordByAdmin() throws Exception {

        this.configureContext("ID_USER_LANNA", "Lanna T.", "lanna@gmail.com", "$2a$10$/Ewn34uMOhPGZFvZtOem9e3Nkf6K/CCkjfMZTBfaeufKXWn7AFRlG");
        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPatchUserAccountChangePasswordByAdmin");
        final String uriChangePassword = fromPath(USER_ACCOUNT_CHANGE_PASSWORD_PATH).buildAndExpand("ID_USER_GUILHERME").toUriString();
        String contentChangePassword = super.getScenarioBody("shouldReturnOkWhenPatchUserAccountChangePassword");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        MvcResult result = mockMvc.perform(patch(uriChangePassword)
                        .header(AUTHORIZATION, authHeader)
                        .content(contentChangePassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(Objects.requireNonNull(result.getResponse().getHeader(LOCATION))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Guilherme Henrik"))
                .andExpect(jsonPath("$.email").value("guilherme@gmail.com"))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles.[0].code").value(USER.name()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return bad request when patch user credential change password where confirmation password and new password are not the same")
    void shouldReturnBadRequestWhenPatchUserAccountChangePasswordWhereConfirmationPasswordAndNewPasswordAreNotTheSame() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForChangePasswordWhereConfirmationPasswordAndNewPasswordAreNotTheSame");
        final String uriChangePassword = fromPath(USER_ACCOUNT_CHANGE_PASSWORD_PATH).buildAndExpand("ID_USER_ALICE").toUriString();
        String contentChangePassword = super.getScenarioBody("shouldReturnBadRequestWhenPatchUserAccountChangePasswordWhereConfirmationPasswordAndNewPasswordAreNotTheSame");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        mockMvc.perform(patch(uriChangePassword)
                        .header(AUTHORIZATION, authHeader)
                        .content(contentChangePassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(WRONG_CONFIRMATION_PASSWORD.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(WRONG_CONFIRMATION_PASSWORD.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uriChangePassword)));
    }

    @Test
    @DisplayName("Should return bad request when patch user credential change password where current password and new password are the same")
    void shouldReturnBadRequestWhenPatchUserAccountChangePasswordWhereCurrentPasswordAndNewPasswordAreTheSame() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForChangePasswordWhereCurrentPasswordAndNewPasswordAreTheSame");
        final String uriChangePassword = fromPath(USER_ACCOUNT_CHANGE_PASSWORD_PATH).buildAndExpand("ID_USER_MARIA").toUriString();
        String contentChangePassword = super.getScenarioBody("shouldReturnBadRequestWhenPatchUserAccountChangePasswordWhereCurrentPasswordAndNewPasswordAreTheSame");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        mockMvc.perform(patch(uriChangePassword)
                        .header(AUTHORIZATION, authHeader)
                        .content(contentChangePassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(SAME_CURRENT_AND_NEW_PASSWORD.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(SAME_CURRENT_AND_NEW_PASSWORD.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uriChangePassword)));
    }

    @Test
    @DisplayName("Should return bad request when patch user credential change password where not authorized")
    void shouldReturnBadRequestWhenPatchUserAccountChangePasswordWhereNotAuthorized() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForChangePasswordWhereNotAuthorize");
        final String uriChangePassword = fromPath(USER_ACCOUNT_CHANGE_PASSWORD_PATH).buildAndExpand("ID_USER_DIEGO").toUriString();
        String contentChangePassword = super.getScenarioBody("shouldReturnBadRequestWhenPatchUserAccountChangePasswordWhereNotAuthorized");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        mockMvc.perform(patch(uriChangePassword)
                        .header(AUTHORIZATION, authHeader)
                        .content(contentChangePassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(NOT_AUTHORIZED.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(NOT_AUTHORIZED.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uriChangePassword)));
    }

    @Test
    @DisplayName("Should return ok when patch user credential add user role")
    void shouldReturnOkWhenPatchUserAccountAddUserRole() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForOkWhenPatchUserAccountAddUserRole");
        final String uri = fromPath(USER_ACCOUNT_CHANGE_ROLE_PATH).buildAndExpand("ID_USER_COCOTA").toUriString();
        String content = super.getScenarioBody("shouldReturnOkWhenPatchUserAccountAddUserRole");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        MvcResult result = mockMvc.perform(patch(uri)
                        .header(AUTHORIZATION, authHeader)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(Objects.requireNonNull(result.getResponse().getHeader(LOCATION))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Cocota Fofinha"))
                .andExpect(jsonPath("$.email").value("cocota@gmail.com"))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles.[0].code").value(ADMIN.name()))
                .andExpect(jsonPath("$.roles.[1].code").value(USER.name()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return ok when patch user credential add two user role")
    void shouldReturnOkWhenPatchUserAccountAddTwoUserRole() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForOkWhenPatchUserAccountAddTwoUserRole");
        final String uri = fromPath(USER_ACCOUNT_CHANGE_ROLE_PATH).buildAndExpand("ID_USER_LOBO").toUriString();
        String content = super.getScenarioBody("shouldReturnOkWhenPatchUserAccountAddTwoUserRole");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        MvcResult result = mockMvc.perform(patch(uri)
                        .header(AUTHORIZATION, authHeader)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(Objects.requireNonNull(result.getResponse().getHeader(LOCATION))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Lobo Cachorro"))
                .andExpect(jsonPath("$.email").value("lobo@gmail.com"))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return Conflict when patch user credential add user role already have")
    void shouldReturnConflictWhenPatchUserAccountAddUserRoleAlreadyHave() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForConflictWhenPatchUserAccountAddUserRoleAlreadyHave");
        final String uri = fromPath(USER_ACCOUNT_CHANGE_ROLE_PATH).buildAndExpand("ID_USER_ARTHUR").toUriString();
        String content = super.getScenarioBody("shouldReturnConflictWhenPatchUserAccountAddUserRoleAlreadyHave");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        mockMvc.perform(patch(uri)
                        .header(AUTHORIZATION, authHeader)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(USER_ALREADY_HAVE_ROLE.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(USER_ALREADY_HAVE_ROLE.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return not found when patch user credential add user role not found")
    void shouldReturnNotFoundWhenPatchUserAccountAddUserRoleNotFound() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForConflictWhenPatchUserAccountAddUserRoleNotFound");
        final String uri = fromPath(USER_ACCOUNT_CHANGE_ROLE_PATH).buildAndExpand("ID_USER_SABRINA").toUriString();
        String content = super.getScenarioBody("shouldReturnNotFoundWhenPatchUserAccountAddUserRoleNotFound");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        mockMvc.perform(patch(uri)
                        .header(AUTHORIZATION, authHeader)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(ROLE_ID_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(ROLE_ID_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return ok when patch user credential remove user role")
    void shouldReturnOkWhenPatchUserAccountRemoveUserRole() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForConflictWhenPatchUserAccountRemoveUserRole");
        final String uri = fromPath(USER_ACCOUNT_CHANGE_ROLE_PATH).buildAndExpand("ID_USER_PAOLO").toUriString();
        String content = super.getScenarioBody("shouldReturnOkWhenPatchUserAccountRemoveUserRole");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        MvcResult result = mockMvc.perform(delete(uri)
                        .header(AUTHORIZATION, authHeader)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(Objects.requireNonNull(result.getResponse().getHeader(LOCATION))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Paolo Spera"))
                .andExpect(jsonPath("$.email").value("paolo@gmail.com"))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles.[0].code").value(ADMIN.name()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    @Test
    @DisplayName("Should return bad request when patch user credential remove user role not have")
    void shouldReturnBadRequestWhenPatchUserAccountRemoveUserRoleNotHave() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticateForConflictWhenPatchUserAccountRemoveUserRoleNotHave");
        final String uri = fromPath(USER_ACCOUNT_CHANGE_ROLE_PATH).buildAndExpand("ID_USER_EMANOELE").toUriString();
        String content = super.getScenarioBody("shouldReturnBadRequestWhenPatchUserAccountRemoveUserRoleNotHave");
        String authHeader = "Bearer " + authenticationResponse.getAccessToken();
        mockMvc.perform(delete(uri)
                        .header(AUTHORIZATION, authHeader)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(USER_NOT_HAVE_ROLE_BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(USER_NOT_HAVE_ROLE_BAD_REQUEST.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(USER_ACCOUNT_RESOURCE_PATH)));
    }

    //TODO Testes de validação de e-mail / token
}
