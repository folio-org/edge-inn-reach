package org.folio.edge.filter;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.folio.edge.config.JwtConfiguration.DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHttpHeaders;
import static org.folio.edge.util.TestUtil.readFileContentAsString;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import org.folio.edge.dto.AccessTokenResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class JwtTokenVerifyFilterTest {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private WireMockServer wireMockServer = new WireMockServer();

  @BeforeEach
  public void setupBeforeEach() {
    wireMockServer.start();
  }

  @AfterEach
  public void tearDownAfterEach() {
    wireMockServer.stop();
  }

  @Test
  void return200HttpCode_when_sendRequestWithValidJwtToken() {
    stubFor(post(urlEqualTo("/inn-reach/authentication")).willReturn(ok()));

    var httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + getJwtToken());

    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/innreach/demo", HttpMethod.GET, requestEntity, String.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    var body = responseEntity.getBody();

    assertNotNull(body);
    assertEquals("Demo!", body);
  }

  private String getJwtToken() {
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

    return body.getAccessToken();
  }

  @Test
  void return401HttpCode_when_sendRequestWithInvalidJwtToken() {
    var invalidJwtToken = readFileContentAsString("/jwt/token/invalid-jwt-token.txt");

    var httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + invalidJwtToken);

    var requestEntity = new HttpEntity<>(httpHeaders);

    var responseEntity = testRestTemplate.exchange("/innreach/demo", HttpMethod.GET, requestEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
  }

  @Test
  void return401HttpCode_when_sendRequestWithoutJwtToken() {
    var requestEntity = new HttpEntity<>(new HttpHeaders());

    var responseEntity = testRestTemplate.exchange("/innreach/demo", HttpMethod.GET, requestEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
  }
}
