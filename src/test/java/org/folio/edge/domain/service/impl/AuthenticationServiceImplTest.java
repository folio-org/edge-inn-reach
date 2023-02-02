package org.folio.edge.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.folio.edge.config.JwtConfiguration.DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHeadersHolder;
import static org.folio.edge.fixture.JwtTokenFixture.createRandomJwtAccessToken;
import static org.folio.edge.util.TestUtil.randomUUIDString;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.folio.edge.domain.exception.EdgeServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import org.folio.edge.client.InnReachAuthClient;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;

class AuthenticationServiceImplTest {

  @Mock
  private InnReachAuthClient innReachAuthClient;

  @Mock
  private AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @BeforeEach
  public void setupBeforeEach() throws Exception {
    MockitoAnnotations.openMocks(this).close();
  }

  @Test
  void returnAccessToken_when_centralServerIsAuthorized() {
    when(innReachAuthClient.authenticateCentralServer(any(), any(), any())).thenReturn(ResponseEntity.ok().build());
    when(accessTokenService.generateAccessToken(any())).thenReturn(createRandomJwtAccessToken(randomUUIDString()));

    var innReachHeadersHolder = createInnReachHeadersHolder();

    var accessToken = authenticationService.authenticate(innReachHeadersHolder);

    verify(innReachAuthClient).authenticateCentralServer(any(), any(), any());

    assertNotNull(accessToken);
    assertNotNull(accessToken.getAccessToken());
    assertNotNull(accessToken.getTokenType());
    assertEquals(BEARER_AUTH_SCHEME, accessToken.getTokenType());
    assertEquals(DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC, accessToken.getExpiresIn());
  }

  @Test
  void shouldThrowEdgeServiceException_when_centralServerIsNotAuthorized() {
    when(innReachAuthClient.authenticateCentralServer(any(), any(), any())).thenReturn(ResponseEntity.status(401).build());
    var innReachHeadersHolder = createInnReachHeadersHolder();

    assertThrows(EdgeServiceException.class, () -> authenticationService.authenticate(innReachHeadersHolder));
    verify(innReachAuthClient).authenticateCentralServer(any(), any(), any());
  }
}
