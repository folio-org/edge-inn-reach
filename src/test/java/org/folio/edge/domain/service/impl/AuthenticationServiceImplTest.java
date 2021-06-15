package org.folio.edge.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.folio.edge.config.JwtConfiguration.DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHeadersHolder;
import static org.folio.edge.fixture.JwtTokenFixture.createRandomJwtAccessToken;
import static org.folio.edge.util.TestUtil.randomUUIDString;

import io.jsonwebtoken.Jwt;
import org.folio.edge.domain.service.AccessTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import org.folio.edge.client.ModInnReachFeignClient;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.impl.AuthenticationServiceImpl;

class AuthenticationServiceImplTest {

  @Mock
  private ModInnReachFeignClient modInnReachFeignClient;

  @Mock
  private AccessTokenService<JwtAccessToken, Jwt> accessTokenService;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @BeforeEach
  public void setupBeforeEach() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void returnAccessToken_when_centralServerIsAuthorized() {
    when(modInnReachFeignClient.authenticateCentralServer(any())).thenReturn(ResponseEntity.ok().build());
    when(accessTokenService.generateAccessToken()).thenReturn(createRandomJwtAccessToken(randomUUIDString()));

    var innReachHeadersHolder = createInnReachHeadersHolder();

    var accessToken = authenticationService.authenticate(innReachHeadersHolder);

    verify(modInnReachFeignClient).authenticateCentralServer(any());

    assertNotNull(accessToken);
    assertNotNull(accessToken.getAccessToken());
    assertNotNull(accessToken.getTokenType());
    assertEquals(BEARER_AUTH_SCHEME, accessToken.getTokenType());
    assertEquals(DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC, accessToken.getExpiresIn());
  }
}
