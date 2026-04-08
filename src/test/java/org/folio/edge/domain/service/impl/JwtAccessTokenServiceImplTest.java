package org.folio.edge.domain.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import static org.folio.edge.fixture.JwtTokenFixture.createRandomJwtAccessToken;
import static org.folio.edge.util.TestUtil.readFileContentAsString;

import java.util.UUID;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import org.folio.edge.config.JwtConfiguration;

@ExtendWith(MockitoExtension.class)
class JwtAccessTokenServiceImplTest {

  private static final byte[] SECRET_KEY = "test-jwt-secret-for-hs256-algo!!".getBytes();

  @Mock
  private JwtConfiguration jwtConfiguration;

  @InjectMocks
  private JwtAccessTokenServiceImpl accessTokenService;

  @BeforeEach
  public void setupBeforeEach() {
    when(jwtConfiguration.getIssuer()).thenReturn("folio");
    when(jwtConfiguration.getSecretKey()).thenReturn(Keys.hmacShaKeyFor(SECRET_KEY));
  }

  @Test
  @Disabled
  void returnJwtAccessToken() {
    when(jwtConfiguration.getExpirationTimeSec()).thenReturn(JwtConfiguration.DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC);
    when(jwtConfiguration.getSignatureAlgorithm()).thenReturn(SignatureAlgorithm.HS256);

    var jwtAccessToken = accessTokenService.generateAccessToken(UUID.randomUUID());

    assertNotNull(jwtAccessToken);
    assertNotNull(jwtAccessToken.getToken());
    assertThat(jwtAccessToken.getExpiresIn(), not(0));
  }

  @Test
  void throwException_when_jwtAccessTokenIsInvalid() {
    var jwtTokenString = readFileContentAsString("/jwt/token/jwt-simple.txt");
    when(jwtConfiguration.getSecretKey()).thenReturn(Keys.hmacShaKeyFor("wrong-jwt-secret-for-hs256-test!".getBytes()));

    var jwtAccessToken = createRandomJwtAccessToken(jwtTokenString);
    assertThrows(BadCredentialsException.class, () -> accessTokenService.verifyAccessToken(jwtAccessToken));
  }

  @Test
  void returnVerifiedParsedJwtToken_when_jwtAccessTokenIsValid() {
    var jwtTokenString = readFileContentAsString("/jwt/token/jwt-simple.txt");
    when(jwtConfiguration.getSecretKey()).thenReturn(Keys.hmacShaKeyFor(SECRET_KEY));

    var jwtAccessToken = createRandomJwtAccessToken(jwtTokenString);
    var jwt = accessTokenService.verifyAccessToken(jwtAccessToken);

    assertNotNull(jwt);
  }

}
