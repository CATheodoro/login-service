package com.theodoro.loginservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.theodoro.loginservice.domains.entities.UserAccount;
import com.theodoro.loginservice.api.rest.models.responses.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static com.theodoro.loginservice.api.rest.endpoints.AuthenticationEndpoint.AUTHENTICATION_RESOURCE_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@Sql({ "/cleanup.sql", "/dataset.sql"})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(expression = "#{environment['spring.profiles.active'] == null}")
public class LoginServiceApplicationTests<T extends LoginServiceApplicationTests<?>> {

	private final T endpointsTest = (T) this;
	private final YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected LoginServiceApplicationTests() {
		super();
		yamlProperties.setResources(
				new ClassPathResource("scenarios/endpoints/" + endpointsTest.getClass().getSimpleName() + ".yml"));
	}

	protected String getScenarioBody(final String scenario) {
		return Objects.requireNonNull(yamlProperties.getObject()).getProperty(scenario + ".body");
	}

	public AuthenticationResponse authenticateCreateToken(String content) throws Exception {

		final String uri = fromPath(AUTHENTICATION_RESOURCE_PATH).toUriString();
		MvcResult result = mockMvc.perform(post(uri)
						.content(this.getScenarioBody(content))
						.contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").exists())
				.andExpect(jsonPath("$.refreshToken").exists())
				.andReturn();

		String responseContent = result.getResponse().getContentAsString();
		return new AuthenticationResponse(JsonPath.read(responseContent, "$.accessToken"), JsonPath.read(responseContent, "$.refreshToken"));
	}

	public void securityContext(UserAccount userAccount){
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(
						userAccount,
						null,
						userAccount.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}