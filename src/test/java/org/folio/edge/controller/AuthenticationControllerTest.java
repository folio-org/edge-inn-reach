package org.folio.edge.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.folio.edge.api.utils.Constants.X_OKAPI_TOKEN;
import static org.folio.edge.config.JwtConfiguration.DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHttpHeaders;
import static org.folio.edge.util.TestUtil.TEST_TOKEN;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.folio.edge.controller.base.BaseControllerTest;
import org.folio.edge.dto.AccessTokenResponse;
import org.folio.edge.dto.Error;
import org.folio.edge.external.InnReachHttpHeaders;

class AuthenticationControllerTest extends BaseControllerTest {

  @Autowired
  private TestRestTemplate testRestTemplate;


  @BeforeEach
  public void setupBeforeEach() {
    wireMock.stubFor(post(urlEqualTo("/authn/login"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.CREATED.value())
            .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));
  }

  @Test
  void return400HttpCode_when_missingRequiredHttpHeader() {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.remove(InnReachHttpHeaders.X_FROM_CODE);

    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}&scope={scope}",
        HttpMethod.POST, requestEntity, Error.class, "client_credentials", "innreach_tp");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    Error body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals("invalid_request", body.getError());
    assertEquals("The x-from-code header is required.", body.getErrorDescription());
  }

  @Test
  void return400HttpCode_when_httpHeaderValueIsInvalid() {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(InnReachHttpHeaders.X_FROM_CODE, "qwe123");

    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}&scope={scope}",
        HttpMethod.POST, requestEntity, Error.class, "client_credentials", "innreach_tp");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    Error body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals("invalid_request", body.getError());
  }

  @Test
  void return400HttpCode_when_missingRequiredRequestParameter() {
    var httpHeaders = createInnReachHttpHeaders();
    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}", HttpMethod.POST,
        requestEntity, Error.class, "client_credentials");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    Error body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals("invalid_request", body.getError());
    assertEquals("The scope parameter is required.", body.getErrorDescription());
  }

  @Test
  void return400HttpCode_when_requestParameterIsInvalid() {
    var httpHeaders = createInnReachHttpHeaders();
    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}&scope={scope}",
        HttpMethod.POST, requestEntity, Error.class, "client_credent", "reach_tp");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    Error body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals("invalid_request", body.getError());
  }

  @ParameterizedTest
  @MethodSource("incorrectFormattedAuthTokenList")
  void return400HttpCode_when_authenticationTokenHasIncorrectFormat(String incorrectFormattedAuthToken) {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, incorrectFormattedAuthToken);

    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}&scope={scope}",
        HttpMethod.POST, requestEntity, Error.class, "client_credentials", "innreach_tp");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    Error body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals("invalid_request", body.getError());
  }

  private static List<String> incorrectFormattedAuthTokenList() {
    return List.of(
        Base64.getEncoder().encodeToString(String.format("%s:%s", UUID.randomUUID(), UUID.randomUUID()).getBytes()),

        String.format("%s %s", BASIC_AUTH_SCHEME, Base64.getEncoder().encodeToString(UUID.randomUUID().toString()
            .getBytes())),

        String.format("%s %s", BASIC_AUTH_SCHEME, Base64.getEncoder().encodeToString(String.format("%s:%s:%s", UUID
            .randomUUID(), UUID.randomUUID(), UUID.randomUUID()).getBytes()))
    );
  }

  @Test
  void return401HttpCode_when_keySecretIsNotAuthenticated() {
    wireMock.stubFor(post(urlEqualTo("/inn-reach/authentication")).willReturn(unauthorized()));

    var httpHeaders = createInnReachHttpHeaders();
    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}&scope={scope}",
        HttpMethod.POST, requestEntity, Error.class, "client_credentials", "innreach_tp");

    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

    var body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals("invalid_token", body.getError());
    assertEquals("Token authentication failed", body.getErrorDescription());
  }

  @Test
  void return503HttpCode_when_modInnReachServiceIsUnavailable() {
    wireMock.stubFor(post(urlEqualTo("/inn-reach/authentication")).willReturn(serviceUnavailable()));

    var httpHeaders = createInnReachHttpHeaders();
    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}&scope={scope}",
        HttpMethod.POST, requestEntity, Error.class, "client_credentials", "innreach_tp");

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());

    var body = responseEntity.getBody();

    assertNotNull(body);
  }

  @Test
  void return200HttpCode_and_validAuthToken_when_keySecretIsSuccessfullyAuthenticated() {
    wireMock.stubFor(post(urlEqualTo("/inn-reach/authentication")).willReturn(ok()));

    var httpHeaders = createInnReachHttpHeaders();
    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/v2/oauth2/token?grant_type={grant_type}&scope={scope}",
        HttpMethod.POST, requestEntity, AccessTokenResponse.class, "client_credentials", "innreach_tp");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    var body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals(BEARER_AUTH_SCHEME, body.getTokenType());
    assertEquals(DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC, body.getExpiresIn());
    assertNotNull(body.getAccessToken());
  }

}
