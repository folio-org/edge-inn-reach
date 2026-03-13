package org.folio.edge.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.folio.edge.config.JwtConfiguration.DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHttpHeaders;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

import org.folio.edge.domain.dto.TenantMapping;
import org.folio.edge.security.service.SecurityService;
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;
import org.folio.spring.model.UserToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.folio.edge.controller.base.BaseControllerTest;

import com.github.tomakehurst.wiremock.client.WireMock;

class AuthenticationControllerTest extends BaseControllerTest {

  private static final String OAUTH_TOKEN_URI = "/innreach/v2/oauth2/token?grant_type=client_credentials&scope=innreach_tp";
  private static final String UNKNOWN_LOCAL_SERVER_BASIC_CREDS = "Basic MjJhZGJlYzYtMWVkYy00YjUzLTk1ZDYtOTA3NmE2OGI3NjM0OmIyNTc1N2U1LWE1NTYtNGNlNS1hNjdjLTMyNGE0MDljOWYwZA==";

  @MockitoBean
  private SecurityService securityService;

  @Autowired
  private MockMvc mockMvc;

  private void setupConnectionParamsMock() {
    var connectionParams = new ConnectionSystemParameters()
      .withOkapiToken(new UserToken("token", Instant.MAX))
      .withTenantId("test");
    when(securityService.getInnReachConnectionParameters(any())).thenReturn(connectionParams);
  }

  private void setupTenantMappingMock() {
    var tenantMapping = new TenantMapping("test-key", "test", "test-user");
    when(securityService.getTenantMappingByLocalServerKey(any())).thenReturn(tenantMapping);
  }

  @Test
  void return400HttpCode_when_httpHeaderValueIsInvalid() throws Exception {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set("Authorization", UNKNOWN_LOCAL_SERVER_BASIC_CREDS);

    when(securityService.getInnReachConnectionParameters(any()))
      .thenThrow(new BadCredentialsException("Tenant mapping not found"));

    mockMvc.perform(post(OAUTH_TOKEN_URI).headers(httpHeaders))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("invalid_token"));
  }

  @Test
  void return400HttpCode_when_missingRequiredRequestParameter() throws Exception {
    setupConnectionParamsMock();
    var httpHeaders = createInnReachHttpHeaders();

    mockMvc.perform(post("/innreach/v2/oauth2/token?grant_type=client_credentials").headers(httpHeaders))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("invalid_request"))
        .andExpect(jsonPath("$.error_description").value("The scope parameter is required."));
  }

  @Test
  void return400HttpCode_when_requestParameterIsInvalid() throws Exception {
    setupConnectionParamsMock();
    var httpHeaders = createInnReachHttpHeaders();

    mockMvc.perform(post("/innreach/v2/oauth2/token?grant_type=client_credent&scope=reach_tp").headers(httpHeaders))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("invalid_request"));
  }

  @ParameterizedTest
  @MethodSource("incorrectFormattedAuthTokenList")
  void return400HttpCode_when_authenticationTokenHasIncorrectFormat(String incorrectFormattedAuthToken) throws Exception {
    setupConnectionParamsMock();
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set("Authorization", incorrectFormattedAuthToken);

    mockMvc.perform(post(OAUTH_TOKEN_URI).headers(httpHeaders))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("invalid_request"));
  }

  private static Stream<String> incorrectFormattedAuthTokenList() {
    return Stream.of(
        Base64.getEncoder().encodeToString(String.format("%s:%s", UUID.randomUUID(), UUID.randomUUID()).getBytes()),
        String.format("%s %s", BASIC_AUTH_SCHEME, Base64.getEncoder().encodeToString(String.format("%s:%s:%s", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()).getBytes()))
    );
  }

  @Test
  void return401HttpCode_when_authorizationTokenHasMissingSecret() throws Exception {
    var token = String.format("%s %s", BASIC_AUTH_SCHEME,
        Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()));
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set("Authorization", token);

    mockMvc.perform(post(OAUTH_TOKEN_URI).headers(httpHeaders))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("invalid_token"));
  }

  @Test
  void return401HttpCode_when_keySecretIsNotAuthenticated() throws Exception {
    setupConnectionParamsMock();
    wireMock.stubFor(WireMock.post(urlEqualTo("/inn-reach/authentication")).willReturn(unauthorized()));

    var httpHeaders = createInnReachHttpHeaders();

    mockMvc.perform(post(OAUTH_TOKEN_URI).headers(httpHeaders))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("invalid_request"));
  }

  @Test
  void return503HttpCode_when_modInnReachServiceIsUnavailable() throws Exception {
    setupConnectionParamsMock();
    wireMock.stubFor(WireMock.post(urlEqualTo("/inn-reach/authentication")).willReturn(serviceUnavailable()));

    var httpHeaders = createInnReachHttpHeaders();

    mockMvc.perform(post(OAUTH_TOKEN_URI).headers(httpHeaders))
        .andExpect(status().isServiceUnavailable());
  }

  @Test
  void return200HttpCode_and_validAuthToken_when_keySecretIsSuccessfullyAuthenticated() throws Exception {
    setupConnectionParamsMock();
    setupTenantMappingMock();
    wireMock.stubFor(WireMock.post(urlEqualTo("/inn-reach/authentication")).willReturn(ok()));

    var httpHeaders = createInnReachHttpHeaders();

    mockMvc.perform(post(OAUTH_TOKEN_URI).headers(httpHeaders))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token_type").value(BEARER_AUTH_SCHEME))
        .andExpect(jsonPath("$.expires_in").value(DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC))
        .andExpect(jsonPath("$.access_token").isNotEmpty());
  }

}
