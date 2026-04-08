package org.folio.edge.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.folio.edge.util.TestUtil.readFileContentAsString;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;

import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationConverterTest {

  private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
  private static final String TEST_JWT_SIGNATURE_SECRET = "test-jwt-secret-for-hs256-algo!!";
  private static final String TEST_PRINCIPAL = "1234567890";

  private static final SecretKey TEST_JWT_SECRET_KEY = Keys.hmacShaKeyFor(
    TEST_JWT_SIGNATURE_SECRET.getBytes()
  );

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @InjectMocks
  private JwtAuthenticationConverter jwtAuthenticationConverter;

  @Test
  void throwException_when_thereIsNoAuthorizationHeader() {
    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    var badCredentialsException = assertThrows(BadCredentialsException.class, () -> jwtAuthenticationConverter.convert(
        httpServletRequest));

    assertEquals("Empty authorization header", badCredentialsException.getMessage());
  }

  @Test
  void throwException_when_authorizationHeaderIsNotBearer() {
    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic qwerty123");

    var badCredentialsException = assertThrows(BadCredentialsException.class, () -> jwtAuthenticationConverter.convert(
        httpServletRequest));

    assertEquals("Invalid authorization scheme", badCredentialsException.getMessage());
  }

  @Test
  void throwException_when_bearerAuthorizationHeaderIsEmpty() {
    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(AUTHENTICATION_SCHEME_BEARER);

    var badCredentialsException = assertThrows(BadCredentialsException.class, () -> jwtAuthenticationConverter.convert(httpServletRequest));

    assertEquals("Empty Bearer authentication token", badCredentialsException.getMessage());
  }

  @Test
  void returnAuthorizationToken_when_authorizationHeaderIsCorrectBearerJwtToken() {
    var jwtTokenString = readFileContentAsString("/jwt/token/jwt-simple.txt");

    var jwt = Jwts.parser()
      .verifyWith(TEST_JWT_SECRET_KEY)
      .build()
      .parseSignedClaims(jwtTokenString);

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(String.format("%s %s",
        AUTHENTICATION_SCHEME_BEARER, jwtTokenString));

    when(accessTokenService.verifyAccessToken(any())).thenReturn(jwt);

    var usernamePasswordAuthenticationToken = jwtAuthenticationConverter.convert(httpServletRequest);

    assertNotNull(usernamePasswordAuthenticationToken);
    assertEquals(TEST_PRINCIPAL, usernamePasswordAuthenticationToken.getPrincipal());
  }

  @Test
  void returnAuthorizationTokenWithAuthorities_when_authorizationHeaderIsCorrectBearerJwtToken() {
    var jwtTokenString = readFileContentAsString("/jwt/token/jwt-with-authorities.txt");

    var jwt = Jwts.parser()
      .verifyWith(TEST_JWT_SECRET_KEY)
      .build()
      .parseSignedClaims(jwtTokenString);

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(String.format("%s %s",
        AUTHENTICATION_SCHEME_BEARER, jwtTokenString));

    when(accessTokenService.verifyAccessToken(any())).thenReturn(jwt);

    var usernamePasswordAuthenticationToken = jwtAuthenticationConverter.convert(httpServletRequest);

    assertNotNull(usernamePasswordAuthenticationToken);
    assertEquals(TEST_PRINCIPAL, usernamePasswordAuthenticationToken.getPrincipal());
  }

}
