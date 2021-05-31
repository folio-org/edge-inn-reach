package org.folio.edge.authentication;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import org.folio.edge.config.JwtConfiguration;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

  private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
  private static final String AUTHORITIES_CLAIMS = "authorities";

  private final JwtConfiguration jwtConfiguration;

  @Override
  public UsernamePasswordAuthenticationToken convert(HttpServletRequest httpServletRequest) {
    var jwtTokenString = getJwtTokenString(httpServletRequest);

    var jwtToken = parseJwtTokenString(jwtTokenString);

    var claims = (Claims) jwtToken.getBody();
    var authoritiesClaims = (List<String>) claims.get(AUTHORITIES_CLAIMS);

    return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, collectGrantedAuthorities(
        authoritiesClaims));
  }

  private String getJwtTokenString(HttpServletRequest httpServletRequest) {
    var validatedAuthorizationHeader = getValidatedAuthorizationHeader(httpServletRequest);
    return validatedAuthorizationHeader.replaceAll(AUTHENTICATION_SCHEME_BEARER, "").trim();
  }

  private String getValidatedAuthorizationHeader(HttpServletRequest httpServletRequest) {
    var authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader == null) {
      throw new BadCredentialsException("Empty authorization header");
    }

    authorizationHeader = authorizationHeader.trim();

    if (!StringUtils.startsWithIgnoreCase(authorizationHeader, AUTHENTICATION_SCHEME_BEARER)) {
      throw new BadCredentialsException("Invalid authorization scheme");
    }

    if (authorizationHeader.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
      throw new BadCredentialsException("Empty Bearer authentication token");
    }

    return authorizationHeader;
  }

  private Jwt<Header, Claims> parseJwtTokenString(String jwtTokenString) {
    try {
      return Jwts.parser()
        .setSigningKey(jwtConfiguration.getSecretKey())
        .requireIssuer(jwtConfiguration.getIssuer())
        .parse(jwtTokenString);
    } catch (JwtException e) {
      throw new BadCredentialsException(e.getMessage());
    }
  }

  // GrantedAuthorities used by Spring security in case if user has some specific permissions, roles, etc.
  private List<GrantedAuthority> collectGrantedAuthorities(List<String> authoritiesClaims) {
    if (authoritiesClaims == null || authoritiesClaims.isEmpty()) {
      return Collections.emptyList();
    }
    return authoritiesClaims.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
  }
}
