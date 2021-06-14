package org.folio.edge.domain.service;


import java.util.Base64;
import java.util.UUID;

import javax.validation.Valid;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.folio.edge.client.ModInnReachFeignClient;
import org.folio.edge.config.JwtConfiguration;
import org.folio.edge.domain.dto.AccessTokenRequest;
import org.folio.edge.domain.dto.modinnreach.CentralServerAuthenticationRequest;
import org.folio.edge.dto.AccessTokenResponse;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;

@RequiredArgsConstructor
@Service
@Slf4j
@Validated
public class AuthenticationServiceImpl implements AuthenticationService {

  private final ModInnReachFeignClient modInnReachFeignClient;
  private final JwtConfiguration jwtConfiguration;

  @Override
  public AccessTokenResponse getAccessToken(@Valid AccessTokenRequest accessTokenRequest) {
    // todo - validate xToCode

    var centralServerAuthenticationRequest = buildCentralServerAuthenticationRequest(accessTokenRequest);

    modInnReachFeignClient.authenticateCentralServer(centralServerAuthenticationRequest);

    return new AccessTokenResponse()
      .accessToken(buildJwtAccessToken(accessTokenRequest))
      .tokenType(BEARER_AUTH_SCHEME)
      .expiresIn(jwtConfiguration.getExpirationTimeSec());
  }

  private CentralServerAuthenticationRequest buildCentralServerAuthenticationRequest(
      AccessTokenRequest accessTokenRequest) {
    var decodedAuthorizationHeader = decodeAuthorizationHeader(accessTokenRequest.getAuthorization());
    var keySecretPair = decodedAuthorizationHeader.split(":");

    return CentralServerAuthenticationRequest.builder()
      .localServerCode(accessTokenRequest.getXFromCode()) // todo - verify
      .key(UUID.fromString(keySecretPair[0])).secret(UUID.fromString(keySecretPair[1]))
      .build();
  }

  private String decodeAuthorizationHeader(String authorization) {
    var decodedAuthorizationHeader = Base64.getDecoder().decode(authorization.replaceAll(BASIC_AUTH_SCHEME, "").trim());
    return new String(decodedAuthorizationHeader);
  }

  private String buildJwtAccessToken(AccessTokenRequest accessTokenRequest) {
    // todo - verify if claim values are correct
    return Jwts.builder()
      .setIssuer(jwtConfiguration.getIssuer())
      .setExpiration(jwtConfiguration.calculateExpirationTime())
      .setSubject(accessTokenRequest.getXFromCode())
      .signWith(jwtConfiguration.getSignatureAlgorithm(), jwtConfiguration.getSecretKey())
      .compact();
  }

}
