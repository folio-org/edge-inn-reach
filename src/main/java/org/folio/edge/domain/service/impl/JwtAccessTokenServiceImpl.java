package org.folio.edge.domain.service.impl;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import org.folio.edge.config.JwtConfiguration;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;

@RequiredArgsConstructor
@Service
public class JwtAccessTokenServiceImpl implements AccessTokenService<JwtAccessToken, Jwt> {

  private final JwtConfiguration jwtConfiguration;

  @Override
  public JwtAccessToken generateAccessToken() {
    return JwtAccessToken
      .builder()
      .token(buildJwtAccessToken())
      .expiresIn(jwtConfiguration.getExpirationTimeSec())
      .build();
  }

  private String buildJwtAccessToken() {
    return Jwts.builder()
      .setIssuer(jwtConfiguration.getIssuer())
      .setExpiration(jwtConfiguration.calculateExpirationTime())
      .signWith(jwtConfiguration.getSignatureAlgorithm(), jwtConfiguration.getSecretKey())
      .compact();
  }

  @Override
  public Jwt verifyAccessToken(JwtAccessToken accessToken) {
    try {
      return Jwts.parser()
        .setSigningKey(jwtConfiguration.getSecretKey())
        .requireIssuer(jwtConfiguration.getIssuer())
        .parse(accessToken.getToken());
    } catch (JwtException e) {
      throw new BadCredentialsException(e.getMessage());
    }
  }

}
