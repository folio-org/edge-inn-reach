package org.folio.edge.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import static org.folio.edge.api.utils.Constants.X_OKAPI_TOKEN;
import static org.folio.edge.config.JwtConfiguration.DEFAULT_SIGNATURE_ALGORITHM;
import static org.folio.edge.external.InnReachHttpHeaders.X_D2IR_AUTHORIZATION;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHttpHeaders;
import static org.folio.edge.util.TestUtil.TEST_TOKEN;
import static org.folio.edge.util.TestUtil.readFileContentAsString;

import javax.crypto.spec.SecretKeySpec;

import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;

import org.folio.edge.controller.base.BaseControllerTest;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;

public class InnReachProxyControllerTest extends BaseControllerTest {

  private static final String BASE_URI = "/innreach/v2";
  public static final UrlPattern LOGIN_URL_PATTERN = urlEqualTo("/authn/login");
  public static final UrlPattern PATRON_VERIFY_URL_PATTERN = urlEqualTo("/inn-reach/d2ir/circ/verifypatron");

  private static final String TEST_JWT_SIGNATURE_SECRET = "secret";

  private static final SecretKeySpec TEST_JWT_SECRET_KEY = new SecretKeySpec(
    TEST_JWT_SIGNATURE_SECRET.getBytes(),
    DEFAULT_SIGNATURE_ALGORITHM.getJcaName()
  );

  public static final String JWT_TOKEN_STRING = readFileContentAsString("/jwt/token/jwt-with-authorities.txt");
  public static final String AUTH_TOKEN_VALUE = String.format("%s %s", "Bearer", JWT_TOKEN_STRING);

  @MockBean
  private AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @BeforeEach
  void mockTest() {
    var jwt = Jwts.parser()
      .setSigningKey(TEST_JWT_SECRET_KEY)
      .parseClaimsJws(JWT_TOKEN_STRING);

    when(accessTokenService.verifyAccessToken(any())).thenReturn(jwt);

    wireMock.stubFor(post(LOGIN_URL_PATTERN)
      .willReturn(aResponse()
        .withStatus(OK.value())
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));
  }

  @Test
  void shouldUpdateRequestHeaders() {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(ACCEPT_ENCODING, "gzip");
    httpHeaders.set(AUTHORIZATION, AUTH_TOKEN_VALUE);

    var requestEntity = new HttpEntity<>(httpHeaders);

    wireMock.stubFor(post(PATRON_VERIFY_URL_PATTERN)
      .willReturn(aResponse().withBody("{}")
        .withStatus(OK.value())
        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));

    testRestTemplate.exchange(BASE_URI + "/circ/verifypatron",
      POST, requestEntity, Object.class);

    wireMock.verify(postRequestedFor(PATRON_VERIFY_URL_PATTERN)
      .withoutHeader(AUTHORIZATION)
      .withoutHeader(ACCEPT_ENCODING)
      .withHeader(X_D2IR_AUTHORIZATION, equalTo(AUTH_TOKEN_VALUE))
    );
  }

  @Test
  void shouldHandleCommonErrors() {
    var httpHeaders = createInnReachHttpHeaders();
    var requestEntity = new HttpEntity<>(httpHeaders);

    wireMock.stubFor(post(PATRON_VERIFY_URL_PATTERN)
      .willReturn(aResponse().withBody("{}")
        .withStatus(400)
        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));

    testRestTemplate.exchange(BASE_URI + "/circ/verifypatron",
      POST, requestEntity, Object.class);

    wireMock.verify(postRequestedFor(PATRON_VERIFY_URL_PATTERN)
      .withHeader(X_D2IR_AUTHORIZATION, equalTo(AUTH_TOKEN_VALUE))
      .with
    );
    // verify(getRequestedFor(urlEqualTo("/baeldung")));
  }

}
