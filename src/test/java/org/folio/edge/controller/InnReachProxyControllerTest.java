package org.folio.edge.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.folio.edge.api.utils.Constants.X_OKAPI_TOKEN;
import static org.folio.edge.config.JwtConfiguration.DEFAULT_SIGNATURE_ALGORITHM;
import static org.folio.edge.external.InnReachHttpHeaders.X_D2IR_AUTHORIZATION;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHttpHeaders;
import static org.folio.edge.util.TestUtil.TEST_TOKEN;
import static org.folio.edge.util.TestUtil.readFileContentAsString;

import javax.crypto.spec.SecretKeySpec;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.folio.edgecommonspring.security.SecurityManagerService;
import org.folio.spring.model.UserToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.folio.edge.controller.base.BaseControllerTest;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class InnReachProxyControllerTest extends BaseControllerTest {

  private static final String BASE_URI = "/innreach/v2";
  private static final UrlPattern LOGIN_URL_PATTERN = urlEqualTo("/authn/login");
  private static final UrlPattern PATRON_VERIFY_URL_PATTERN = urlEqualTo("/inn-reach/d2ir/circ/verifypatron");

  private static final String TEST_JWT_SIGNATURE_SECRET = "secret";
  private static final SecretKeySpec TEST_JWT_SECRET_KEY = new SecretKeySpec(
    TEST_JWT_SIGNATURE_SECRET.getBytes(),
    DEFAULT_SIGNATURE_ALGORITHM.getJcaName()
  );

  private static final String JWT_TOKEN_STRING = readFileContentAsString("/jwt/token/jwt-with-authorities.txt");
  private static final String AUTH_TOKEN_VALUE = String.format("%s %s", "Bearer", JWT_TOKEN_STRING);

  @MockitoBean
  private AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @MockitoBean
  private SecurityManagerService securityManagerService;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    var jwt = Jwts.parser()
      .setSigningKey(TEST_JWT_SECRET_KEY)
      .parseClaimsJws(JWT_TOKEN_STRING);

    when(accessTokenService.verifyAccessToken(any())).thenReturn(jwt);
    when(securityManagerService.getParamsWithToken(any())).thenReturn(
      new ConnectionSystemParameters().withOkapiToken(new UserToken("token", Instant.MAX)).withTenantId("test"));

    wireMock.stubFor(WireMock.post(LOGIN_URL_PATTERN)
      .willReturn(aResponse()
        .withStatus(OK.value())
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));
  }

  @Test
  void shouldUpdateRequestHeaders() throws Exception {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(ACCEPT_ENCODING, "gzip");
    httpHeaders.set(AUTHORIZATION, AUTH_TOKEN_VALUE);

    wireMock.stubFor(WireMock.post(PATRON_VERIFY_URL_PATTERN)
      .willReturn(aResponse().withBody("{}")
        .withStatus(OK.value())
        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));

    mockMvc.perform(post(BASE_URI + "/circ/verifypatron").headers(httpHeaders))
      .andExpect(status().isOk());

    wireMock.verify(postRequestedFor(PATRON_VERIFY_URL_PATTERN)
      .withoutHeader(AUTHORIZATION)
      .withoutHeader(ACCEPT_ENCODING)
      .withHeader(X_D2IR_AUTHORIZATION, equalTo(AUTH_TOKEN_VALUE))
    );
  }

  @Test
  void shouldHandleCommonErrors() throws Exception {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(AUTHORIZATION, AUTH_TOKEN_VALUE);

    wireMock.stubFor(WireMock.post(PATRON_VERIFY_URL_PATTERN)
      .willReturn(aResponse().withBody(readFileContentAsString("/error/common-error.json"))
        .withStatus(BAD_REQUEST.value())
        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));

    mockMvc.perform(post(BASE_URI + "/circ/verifypatron").headers(httpHeaders))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value("failed"))
      .andExpect(jsonPath("$.reason").value("Appeared common error"));
  }

  @Test
  void shouldHandleCommonErrorsWhenCantReadBody() throws Exception {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(AUTHORIZATION, AUTH_TOKEN_VALUE);

    wireMock.stubFor(WireMock.post(PATRON_VERIFY_URL_PATTERN)
      .willReturn(aResponse().withBody("Plain text error msg")
        .withStatus(BAD_REQUEST.value())
        .withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE)
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));

    mockMvc.perform(post(BASE_URI + "/circ/verifypatron").headers(httpHeaders))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value("failed"))
      .andExpect(jsonPath("$.reason").value("Plain text error msg"));
  }

}
