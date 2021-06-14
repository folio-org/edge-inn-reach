package org.folio.edge.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.folio.edge.config.JwtConfiguration.DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;
import static org.folio.edge.fixture.InnReachFixture.createAccessTokenRequest;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import org.folio.edge.client.ModInnReachFeignClient;
import org.folio.edge.config.JwtConfiguration;

class AuthenticationServiceImplTest {

  @Mock
  private ModInnReachFeignClient modInnReachFeignClient;

  @Mock
  private JwtConfiguration jwtConfiguration;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @BeforeEach
  public void setupBeforeEach() {
    MockitoAnnotations.initMocks(this);

    when(jwtConfiguration.getIssuer()).thenReturn("folio");
    when(jwtConfiguration.getExpirationTimeSec()).thenReturn(DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC);
    when(jwtConfiguration.getSignatureAlgorithm()).thenReturn(SignatureAlgorithm.HS256);
    when(jwtConfiguration.getSecretKey()).thenReturn(new SecretKeySpec("secret".getBytes(), SignatureAlgorithm.HS256.getJcaName()));
  }

  @Test
  void returnAccessToken_when_centralServerIsAuthorized() {
    when(modInnReachFeignClient.authenticateCentralServer(any())).thenReturn(ResponseEntity.ok().build());

     var accessTokenRequest = createAccessTokenRequest();

    var accessToken = authenticationService.getAccessToken(accessTokenRequest);

    verify(modInnReachFeignClient).authenticateCentralServer(any());

    assertNotNull(accessToken);
    assertNotNull(accessToken.getAccessToken());
    assertNotNull(accessToken.getTokenType());
    assertEquals(BEARER_AUTH_SCHEME, accessToken.getTokenType());
    assertEquals(DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC, accessToken.getExpiresIn());
  }
}
