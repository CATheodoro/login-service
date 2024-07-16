package com.theodoro.loginservice.endpoints;

import com.theodoro.loginservice.LoginServiceApplicationTests;
import com.theodoro.loginservice.api.rest.models.responses.AuthenticationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.BAD_CREDENTIALS;
import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.JWT_NOT_VALIDITY;
import static com.theodoro.loginservice.api.rest.endpoints.AuthenticationEndpoint.AUTHENTICATION_REFRESH_TOKEN_PATH;
import static com.theodoro.loginservice.api.rest.endpoints.AuthenticationEndpoint.AUTHENTICATION_RESOURCE_PATH;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@DisplayName("AuthenticationEndpoint Test")
class AuthenticationEndpointTest extends LoginServiceApplicationTests<AuthenticationEndpointTest> {

    @Test
    @DisplayName("Should return ok when post on authentication")
    void shouldReturnOkWhenPostAuthenticationAuthenticate() throws Exception {
        super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticate");
    }

    @Test
    @DisplayName("Should return bad request when post with wrong password")
    void shouldReturnBadRequestWhenPostWithWrongPassword() throws Exception {

        final String uri = fromPath(AUTHENTICATION_RESOURCE_PATH).toUriString();
        String content = super.getScenarioBody("shouldReturnBadRequestWhenPostWithWrongPassword");
        mockMvc.perform(post(uri)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(BAD_CREDENTIALS.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(BAD_CREDENTIALS.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return ok when refresh token is valid")
    void shouldReturnOkWhenRefreshTokenIsValid() throws Exception {

        AuthenticationResponse authenticationResponse = super.authenticateCreateToken("shouldReturnOkWhenPostAuthenticationAuthenticate");
        final String uriRefresh = fromPath(AUTHENTICATION_REFRESH_TOKEN_PATH).toUriString();
        String authHeader = "Bearer " + authenticationResponse.getRefreshToken();

        mockMvc.perform(post(uriRefresh)
                        .header(AUTHORIZATION, authHeader))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("Should return bad request when refresh token is expired")
    void shouldReturnBadRequestWhenRefreshTokenIsExpired() throws Exception {

        final String uri = fromPath(AUTHENTICATION_REFRESH_TOKEN_PATH).toUriString();
        String authHeader = "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjYXJsb3NAZ21haWwuY29tIiwiaWF0IjoxNzE3Nzg1MDc2LCJleHAiOjE3MTc3ODUwNzYsImF1dGhvcml0aWVzIjpbIlVTRVIiLCJVU0VSIl0sImxhc3RVcGRhdGVQYXNzd29yZCI6IjIwMjQtMDYtMDdUMTg6MjU6MjYuMTE1OTA2WiJ9.gXuGcdA1eLVxXD3SgnWsSvbMCpBPkX0v_1NEWc2tjr7lWrXHqxy8T6ZIuZ4HB5vEscf_RIq7cGqr80sw2yxsHg";

        mockMvc.perform(post(uri)
                        .header(AUTHORIZATION, authHeader))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(JWT_NOT_VALIDITY.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(JWT_NOT_VALIDITY.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }
}