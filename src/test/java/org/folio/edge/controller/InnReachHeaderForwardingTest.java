package org.folio.edge.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.folio.edge.api.utils.Constants.X_OKAPI_TOKEN;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHttpHeaders;
import static org.folio.edge.util.TestUtil.TEST_TOKEN;
import static org.folio.edge.util.TestUtil.readFileContentAsString;

import java.time.Instant;

import javax.crypto.SecretKey;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.folio.edgecommonspring.security.SecurityManagerService;
import org.folio.spring.model.UserToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.folio.edge.controller.base.BaseControllerTest;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class InnReachHeaderForwardingTest extends BaseControllerTest {

  private static final String EDGE_BASE_URI = "/innreach/v2";
  private static final String TRACKING_ID = "tracking01";
  private static final String CENTRAL_CODE = "d2irm";

  private static final String MOD_PATRON_HOLD_URI =
    "/inn-reach/d2ir/circ/patronhold/" + TRACKING_ID + "/" + CENTRAL_CODE;
  private static final String MOD_ITEM_SHIPPED_URI =
    "/inn-reach/d2ir/circ/itemshipped/" + TRACKING_ID + "/" + CENTRAL_CODE;

  private static final String TEST_JWT_SIGNATURE_SECRET = "test-jwt-secret-for-hs256-algo!!";
  private static final SecretKey TEST_JWT_SECRET_KEY = Keys.hmacShaKeyFor(
    TEST_JWT_SIGNATURE_SECRET.getBytes()
  );

  private static final String JWT_TOKEN_STRING =
    readFileContentAsString("/jwt/token/jwt-with-authorities.txt");
  private static final String AUTH_TOKEN_VALUE = "Bearer " + JWT_TOKEN_STRING;

  @MockitoBean
  private AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @MockitoBean
  private SecurityManagerService securityManagerService;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    var jwt = Jwts.parser()
      .verifyWith(TEST_JWT_SECRET_KEY)
      .build()
      .parseSignedClaims(JWT_TOKEN_STRING);

    when(accessTokenService.verifyAccessToken(any())).thenReturn(jwt);
    when(securityManagerService.getParamsWithToken(any())).thenReturn(
      new ConnectionSystemParameters()
        .withOkapiToken(new UserToken("token", Instant.MAX))
        .withTenantId("test"));

    wireMock.stubFor(WireMock.post(urlEqualTo("/authn/login"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));
  }

  @Test
  void shouldForwardAcceptAndContentTypeOnPatronHoldPost() throws Exception {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(AUTHORIZATION, AUTH_TOKEN_VALUE);

    wireMock.stubFor(WireMock.post(urlEqualTo(MOD_PATRON_HOLD_URI))
      .willReturn(aResponse()
        .withStatus(200)
        .withBody("{}")
        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    mockMvc.perform(post(EDGE_BASE_URI + "/circ/patronhold/" + TRACKING_ID + "/" + CENTRAL_CODE)
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isOk());

    wireMock.verify(postRequestedFor(urlEqualTo(MOD_PATRON_HOLD_URI))
      .withHeader(ACCEPT, equalTo(APPLICATION_JSON_VALUE))
      .withHeader(CONTENT_TYPE, containing(APPLICATION_JSON_VALUE)));
  }

  @Test
  void shouldForwardAcceptAndContentTypeOnItemShippedPut() throws Exception {
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(AUTHORIZATION, AUTH_TOKEN_VALUE);

    wireMock.stubFor(WireMock.put(urlEqualTo(MOD_ITEM_SHIPPED_URI))
      .willReturn(aResponse()
        .withStatus(200)
        .withBody("{}")
        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    mockMvc.perform(put(EDGE_BASE_URI + "/circ/itemshipped/" + TRACKING_ID + "/" + CENTRAL_CODE)
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isOk());

    wireMock.verify(putRequestedFor(urlEqualTo(MOD_ITEM_SHIPPED_URI))
      .withHeader(ACCEPT, equalTo(APPLICATION_JSON_VALUE))
      .withHeader(CONTENT_TYPE, containing(APPLICATION_JSON_VALUE)));
  }

  @Test
  void shouldSendAcceptHeaderEvenWhenNotExplicitlySetByClient() throws Exception {
    // Client sends no Accept header — InnReachClient's @HttpExchange(accept=APPLICATION_JSON_VALUE)
    // must still ensure Accept: application/json reaches mod-inn-reach
    var httpHeaders = createInnReachHttpHeaders();
    httpHeaders.set(AUTHORIZATION, AUTH_TOKEN_VALUE);

    wireMock.stubFor(WireMock.post(urlEqualTo(MOD_PATRON_HOLD_URI))
      .willReturn(aResponse()
        .withStatus(200)
        .withBody("{}")
        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    mockMvc.perform(post(EDGE_BASE_URI + "/circ/patronhold/" + TRACKING_ID + "/" + CENTRAL_CODE)
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isOk());

    wireMock.verify(postRequestedFor(urlEqualTo(MOD_PATRON_HOLD_URI))
      .withHeader(ACCEPT, containing(APPLICATION_JSON_VALUE)));
  }
}
