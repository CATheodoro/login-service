package com.theodoro.loginservice.endpoints;

import com.theodoro.loginservice.LoginServiceApplicationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.ROLE_ALREADY_EXISTS;
import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.ROLE_ID_NOT_FOUND;
import static com.theodoro.loginservice.api.rest.endpoints.RoleEndpoint.ROLE_RESOURCE_PATH;
import static com.theodoro.loginservice.api.rest.endpoints.RoleEndpoint.ROLE_SELF_PATH;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@DisplayName("RoleEndpoint Test")
class RoleEndpointTest extends LoginServiceApplicationTests<RoleEndpointTest> {

    @Test
    @DisplayName("Should return ok when get role findAll")
    void shouldReturnOkWhenGetRoleFindAll() throws Exception {

        final String uri = fromPath(ROLE_RESOURCE_PATH).toUriString();
        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("ID_ROLE_USER"))
                .andExpect(jsonPath("$[0].code").value("USER"))
                .andExpect(jsonPath("$[0].description").value("Pouca permissão"))
                .andExpect(jsonPath("$[0].creationDate").exists())
                .andExpect(jsonPath("$[0].links[0].href").value(containsString(uri)))

                .andExpect(jsonPath("$[1].id").value("ID_ROLE_ADMIN"))
                .andExpect(jsonPath("$[1].code").value("ADMIN"))
                .andExpect(jsonPath("$[1].description").value("Acesso total"))
                .andExpect(jsonPath("$[1].creationDate").exists())
                .andExpect(jsonPath("$[1].links[0].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return created when post save role")
    void shouldReturnCreatedWhenPostRoleSave() throws Exception {

        final String uri = fromPath(ROLE_RESOURCE_PATH).toUriString();
        String content = super.getScenarioBody("shouldReturnCreatedWhenPostRoleSave");
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
                .andExpect(jsonPath("$.code").value("USER_PLUS"))
                .andExpect(jsonPath("$.description").value("Pouca permissão, com benefícios."))
                .andExpect(jsonPath("$.creationDate").exists())
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return conflict when post role already exists")
    void shouldReturnConflictWhenPostRoleAlreadyExists() throws Exception {

        final String uri = fromPath(ROLE_RESOURCE_PATH).toUriString();
        String content = super.getScenarioBody("shouldReturnConflictWhenPostRoleAlreadyExists");
        mockMvc.perform(post(uri)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(ROLE_ALREADY_EXISTS.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(ROLE_ALREADY_EXISTS.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return ok when get role findById")
    void shouldReturnOkWhenGetRoleFindById() throws Exception {

        final String uri = fromPath(ROLE_SELF_PATH).buildAndExpand("ID_ROLE_USER").toUriString();
        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ID_ROLE_USER"))
                .andExpect(jsonPath("$.code").value("USER"))
                .andExpect(jsonPath("$.description").value("Pouca permissão"))
                .andExpect(jsonPath("$.creationDate").exists())
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }

    @Test
    @DisplayName("Should return not found when get role findById")
    void shouldReturnNotFoundWhenGetRoleFindById() throws Exception {

        final String uri = fromPath(ROLE_SELF_PATH).buildAndExpand("-1").toUriString();
        mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.messages").exists())
                .andExpect(jsonPath("$.errors.messages[0].code").value(ROLE_ID_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errors.messages[0].message").value(ROLE_ID_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$._links['self'].href").value(containsString(uri)));
    }
}
