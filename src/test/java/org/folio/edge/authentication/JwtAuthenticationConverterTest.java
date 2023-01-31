package org.folio.edge.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.folio.edge.config.JwtConfiguration.DEFAULT_SIGNATURE_ALGORITHM;
import static org.folio.edge.util.TestUtil.readFileContentAsString;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;

class JwtAuthenticationConverterTest {

  private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
  private static final String TEST_JWT_SIGNATURE_SECRET = "secret";
  private static final String TEST_PRINCIPAL = "1234567890";

  private static final SecretKeySpec TEST_JWT_SECRET_KEY = new SecretKeySpec(
    TEST_JWT_SIGNATURE_SECRET.getBytes(),
    DEFAULT_SIGNATURE_ALGORITHM.getJcaName()
  );

  private static final List<SimpleGrantedAuthority> TEST_AUTHORITIES = List.of(
    new SimpleGrantedAuthority("authority_a"),
    new SimpleGrantedAuthority("authority_b")
  );

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @InjectMocks
  private JwtAuthenticationConverter jwtAuthenticationConverter;

  @BeforeEach
  public void setupBeforeEach() {
    MockitoAnnotations.initMocks(this);
  }

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
      .setSigningKey(TEST_JWT_SECRET_KEY)
      .parseClaimsJws(jwtTokenString);

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
      .setSigningKey(TEST_JWT_SECRET_KEY)
      .parseClaimsJws(jwtTokenString);

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(String.format("%s %s",
        AUTHENTICATION_SCHEME_BEARER, jwtTokenString));

    when(accessTokenService.verifyAccessToken(any())).thenReturn(jwt);

    var usernamePasswordAuthenticationToken = jwtAuthenticationConverter.convert(httpServletRequest);

    assertNotNull(usernamePasswordAuthenticationToken);
    assertEquals(TEST_PRINCIPAL, usernamePasswordAuthenticationToken.getPrincipal());
  }

}
