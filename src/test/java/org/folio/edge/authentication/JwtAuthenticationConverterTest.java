package org.folio.edge.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import static org.folio.edge.util.TestUtils.readStringFromFile;

import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.folio.edge.config.JwtConfiguration;

class JwtAuthenticationConverterTest {

  private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
  private static final String TEST_JWT_SIGNATURE_SECRET = "secret";
  private static final String TEST_PRINCIPAL = "1234567890";

  private static final SecretKeySpec TEST_JWT_SECRET_KEY = new SecretKeySpec(TEST_JWT_SIGNATURE_SECRET.getBytes(),
      SignatureAlgorithm.HS256.getJcaName());

  private static final List<SimpleGrantedAuthority> TEST_AUTHORITIES = List.of(new SimpleGrantedAuthority(
      "authority_a"), new SimpleGrantedAuthority("authority_b"));

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private JwtConfiguration jwtConfiguration;

  @InjectMocks
  private JwtAuthenticationConverter jwtAuthenticationConverter;

  @BeforeEach
  private void setupBeforeEach() {
    MockitoAnnotations.initMocks(this);

    when(jwtConfiguration.getSecretKey()).thenReturn(TEST_JWT_SECRET_KEY);
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
    var jwtTokenString = readStringFromFile("/jwt/token/jwt-simple.txt");

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(String.format("%s %s",
        AUTHENTICATION_SCHEME_BEARER, jwtTokenString));

    var usernamePasswordAuthenticationToken = jwtAuthenticationConverter.convert(httpServletRequest);

    assertNotNull(usernamePasswordAuthenticationToken);
    assertEquals(TEST_PRINCIPAL, usernamePasswordAuthenticationToken.getPrincipal());
  }

  @Test
  void returnAuthorizationTokenWithAuthorities_when_authorizationHeaderIsCorrectBearerJwtToken() {
    var jwtTokenString = readStringFromFile("/jwt/token/jwt-with-authorities.txt");

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(String.format("%s %s",
        AUTHENTICATION_SCHEME_BEARER, jwtTokenString));

    var usernamePasswordAuthenticationToken = jwtAuthenticationConverter.convert(httpServletRequest);

    assertNotNull(usernamePasswordAuthenticationToken);
    assertEquals(TEST_PRINCIPAL, usernamePasswordAuthenticationToken.getPrincipal());

    assertEquals(TEST_AUTHORITIES, usernamePasswordAuthenticationToken.getAuthorities());
  }

}
