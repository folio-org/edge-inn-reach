package org.folio.edge.domain.service.impl;

import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import org.folio.edge.config.JwtConfiguration;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edge.security.service.SecurityService;
import org.folio.edge.utils.CredentialsUtils;

@RequiredArgsConstructor
@Service
@Log4j2
public class JwtAccessTokenServiceImpl implements AccessTokenService<JwtAccessToken, Jws<Claims>> {

  public static final String EDGE_API_KEY = "edgeApiKey";

  private final JwtConfiguration jwtConfiguration;
  private final SecurityService securityService;

  @Override
  public JwtAccessToken generateAccessToken(UUID localServerKey) {
    log.debug("Generate the access token for the localServerKey");
    return JwtAccessToken
      .builder()
      .token(buildJwtAccessToken(localServerKey))
      .expiresIn(jwtConfiguration.getExpirationTimeSec())
      .build();
  }

  private String buildJwtAccessToken(UUID localServerKey) {
    log.debug("Build the JWT Access Token for the local server key.");
    return Jwts.builder()
      .setIssuer(jwtConfiguration.getIssuer())
      .setSubject(localServerKey.toString())
      .claim(EDGE_API_KEY, generateEdgeApiKey(localServerKey))
      .setExpiration(jwtConfiguration.calculateExpirationTime())
      .signWith(jwtConfiguration.getSignatureAlgorithm(), jwtConfiguration.getSecretKey())
      .compact();
  }

  private String generateEdgeApiKey(UUID localServerKey) {
    log.debug("generateEdgeApiKey :: parameter localServerKey : {}", localServerKey);
    var tenantMapping = securityService.getTenantMappingByLocalServerKey(localServerKey);
    log.info("Edge API Key generated");
    return CredentialsUtils.generateApiKey(tenantMapping.getUsername(), tenantMapping.getTenantId(), tenantMapping.getUsername());
  }

  @Override
  public Jws<Claims> verifyAccessToken(JwtAccessToken accessToken) {
    try {
      log.debug("Verify access token");
      return Jwts.parser()
        .setSigningKey(jwtConfiguration.getSecretKey())
        .requireIssuer(jwtConfiguration.getIssuer())
        .parseClaimsJws(accessToken.getToken());
    } catch (JwtException e) {
      log.warn("Bad credentials");
      throw new BadCredentialsException(e.getMessage());
    }
  }

}
