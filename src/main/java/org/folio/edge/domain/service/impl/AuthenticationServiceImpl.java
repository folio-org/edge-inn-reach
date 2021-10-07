package org.folio.edge.domain.service.impl;


import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;

import java.util.Base64;
import java.util.UUID;

import javax.validation.Valid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.folio.edge.client.InnReachAuthClient;
import org.folio.edge.domain.dto.AuthenticationParams;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.dto.modinnreach.CentralServerAuthenticationRequest;
import org.folio.edge.domain.exception.EdgeServiceException;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edge.domain.service.AuthenticationService;
import org.folio.edge.dto.AccessTokenResponse;

@RequiredArgsConstructor
@Service
@Log4j2
@Validated
public class AuthenticationServiceImpl implements AuthenticationService {

  private static final String AUTHENTICATION_TOKEN_KEY_SECRET_DELIMITER = ":";
  private static final int KEY_POSITION_IN_TOKEN = 0;
  private static final int SECRET_POSITION_IN_TOKEN = 1;

  private final InnReachAuthClient innReachAuthClient;
  private final AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @Override
  public AccessTokenResponse authenticate(@Valid AuthenticationParams authParams) {
    var authenticationRequest = buildCentralServerAuthenticationRequest(authParams);

    var authResult = innReachAuthClient.authenticateCentralServer(authenticationRequest,
      authParams.getOkapiTenant(), authParams.getOkapiToken());

    if (!authResult.getStatusCode().is2xxSuccessful()) {
      log.debug("Authentication failed with status: {}", authResult.getStatusCodeValue());
      throw new EdgeServiceException("Authentication failed");
    }

    var jwtAccessToken = accessTokenService.generateAccessToken(authParams.getXFromCode(), authParams.getXToCode());

    return new AccessTokenResponse()
      .accessToken(jwtAccessToken.getToken())
      .tokenType(BEARER_AUTH_SCHEME)
      .expiresIn(jwtAccessToken.getExpiresIn());
  }

  private CentralServerAuthenticationRequest buildCentralServerAuthenticationRequest(AuthenticationParams params) {
    var decodedAuthorizationHeader = decodeAuthorizationHeader(params.getAuthorization());
    var keySecretArray = decodedAuthorizationHeader.split(AUTHENTICATION_TOKEN_KEY_SECRET_DELIMITER);

    return CentralServerAuthenticationRequest.builder()
      .localServerCode(params.getXToCode())
      .key(UUID.fromString(keySecretArray[KEY_POSITION_IN_TOKEN]))
      .secret(UUID.fromString(keySecretArray[SECRET_POSITION_IN_TOKEN]))
      .build();
  }

  private String decodeAuthorizationHeader(String authorization) {
    var decodedAuthorizationHeader = Base64.getDecoder()
      .decode(authorization.replaceAll(BASIC_AUTH_SCHEME, "").trim());

    return new String(decodedAuthorizationHeader);
  }

}
