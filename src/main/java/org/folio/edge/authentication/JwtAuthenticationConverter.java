package org.folio.edge.authentication;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

  private static final String AUTHORITIES_CLAIMS = "authorities";

  private final AccessTokenService<JwtAccessToken, Jwt> accessTokenService;

  @Override
  public UsernamePasswordAuthenticationToken convert(HttpServletRequest httpServletRequest) {
    var jwtAccessToken = extractJwtTokenFromHeader(httpServletRequest);

    var verifiedJwtToken = accessTokenService.verifyAccessToken(jwtAccessToken);

    var claims = (Claims) verifiedJwtToken.getBody();
    var authoritiesClaims = (List<String>) claims.get(AUTHORITIES_CLAIMS);

    return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, collectGrantedAuthorities(
        authoritiesClaims));
  }

  private JwtAccessToken extractJwtTokenFromHeader(HttpServletRequest httpServletRequest) {
    var validatedAuthorizationHeader = getValidatedAuthorizationHeader(httpServletRequest);
    var jwtToken = validatedAuthorizationHeader.replaceAll(BEARER_AUTH_SCHEME, "").trim();

    return JwtAccessToken
      .builder()
      .token(jwtToken)
      .build();
  }

  private String getValidatedAuthorizationHeader(HttpServletRequest httpServletRequest) {
    var authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader == null) {
      throw new BadCredentialsException("Empty authorization header");
    }

    authorizationHeader = authorizationHeader.trim();

    if (!StringUtils.startsWithIgnoreCase(authorizationHeader, BEARER_AUTH_SCHEME)) {
      throw new BadCredentialsException("Invalid authorization scheme");
    }

    if (authorizationHeader.equalsIgnoreCase(BEARER_AUTH_SCHEME)) {
      throw new BadCredentialsException("Empty Bearer authentication token");
    }

    return authorizationHeader;
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
