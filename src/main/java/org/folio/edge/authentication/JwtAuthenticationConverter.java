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
    log.debug("Extract token and verify it :: parameter: {} ", httpServletRequest.toString());
    var jwtAccessToken = extractJwtTokenFromHeader(httpServletRequest);

    var verifiedJwtToken = accessTokenService.verifyAccessToken(jwtAccessToken);
    EdgeApiKeyHolder.setEdgeApiKey((String) verifiedJwtToken.getBody().get(EDGE_API_KEY_CLAIM));

    log.info("UsernamePasswordAuthenticationToken generated.");
    return new UsernamePasswordAuthenticationToken(verifiedJwtToken.getBody().getSubject(), null, Collections.emptyList());
  }

  private JwtAccessToken extractJwtTokenFromHeader(HttpServletRequest httpServletRequest) {
    log.debug("extractJwtTokenFromHeader :: parameter httpServletRequest: {} ", httpServletRequest.toString());
    var validatedAuthorizationHeader = getValidatedAuthorizationHeader(httpServletRequest);
    var jwtToken = validatedAuthorizationHeader.replaceAll(BEARER_AUTH_SCHEME, "").trim();

    log.info("JWT token extracted from header.");
    return JwtAccessToken
      .builder()
      .token(jwtToken)
      .build();
  }

  private String getValidatedAuthorizationHeader(HttpServletRequest httpServletRequest) {
    log.debug("getValidatedAuthorizationHeader :: parameter httpServletRequest: {} ",httpServletRequest.toString());
    var authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader == null) {
      log.warn("Empty authorization header");
      throw new BadCredentialsException("Empty authorization header");
    }

    authorizationHeader = authorizationHeader.trim();

    if (!StringUtils.startsWithIgnoreCase(authorizationHeader, BEARER_AUTH_SCHEME)) {
      log.warn("Invalid authorization scheme");
      throw new BadCredentialsException("Invalid authorization scheme");
    }

    if (authorizationHeader.equalsIgnoreCase(BEARER_AUTH_SCHEME)) {
      log.warn("Empty Bearer authentication token");
      throw new BadCredentialsException("Empty Bearer authentication token");
    }
    log.info("Authorization Header is: {} ",authorizationHeader);
    return authorizationHeader;
  }

}
