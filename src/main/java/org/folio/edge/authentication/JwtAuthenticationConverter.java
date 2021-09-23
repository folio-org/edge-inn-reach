package org.folio.edge.authentication;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edge.security.store.EdgeApiKeyHolder;

@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

  private static final String EDGE_API_KEY_CLAIM = "edgeApiKey";

  private final AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @Override
  public UsernamePasswordAuthenticationToken convert(HttpServletRequest httpServletRequest) {
    var jwtAccessToken = extractJwtTokenFromHeader(httpServletRequest);

    var verifiedJwtToken = accessTokenService.verifyAccessToken(jwtAccessToken);
    EdgeApiKeyHolder.setEdgeApiKey((String) verifiedJwtToken.getBody().get(EDGE_API_KEY_CLAIM));

    return new UsernamePasswordAuthenticationToken(verifiedJwtToken.getBody().getSubject(), null, Collections.emptyList());
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

}
