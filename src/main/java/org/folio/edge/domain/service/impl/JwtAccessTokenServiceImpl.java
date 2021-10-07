package org.folio.edge.domain.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import org.folio.edge.config.JwtConfiguration;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edge.security.service.SecurityService;
import org.folio.edge.utils.ApiKeyUtils;

@RequiredArgsConstructor
@Service
public class JwtAccessTokenServiceImpl implements AccessTokenService<JwtAccessToken, Jws<Claims>> {

  public static final String EDGE_API_KEY = "edgeApiKey";

  private final JwtConfiguration jwtConfiguration;
  private final SecurityService securityService;

  @Override
  public JwtAccessToken generateAccessToken(String xFromCode, String xToCode) {
    return JwtAccessToken
      .builder()
      .token(buildJwtAccessToken(xFromCode, xToCode))
      .expiresIn(jwtConfiguration.getExpirationTimeSec())
      .build();
  }

  private String buildJwtAccessToken(String xFromCode, String xToCode) {
    return Jwts.builder()
      .setIssuer(jwtConfiguration.getIssuer())
      .setSubject(xFromCode)
      .claim(EDGE_API_KEY, generateEdgeApiKey(xToCode))
      .setExpiration(jwtConfiguration.calculateExpirationTime())
      .signWith(jwtConfiguration.getSignatureAlgorithm(), jwtConfiguration.getSecretKey())
      .compact();
  }

  private String generateEdgeApiKey(String xToCode) {
    var tenantMapping = securityService.getTenantMappingByXToCode(xToCode);
    
    return ApiKeyUtils.generateApiKey(tenantMapping.getUsername(), tenantMapping.getTenantId(), tenantMapping.getUsername());
  }

  @Override
  public Jws<Claims> verifyAccessToken(JwtAccessToken accessToken) {
    try {
      return Jwts.parser()
        .setSigningKey(jwtConfiguration.getSecretKey())
        .requireIssuer(jwtConfiguration.getIssuer())
        .parseClaimsJws(accessToken.getToken());
    } catch (JwtException e) {
      throw new BadCredentialsException(e.getMessage());
    }
  }

}
