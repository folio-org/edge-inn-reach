package org.folio.edge.domain.service.impl;


import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;
import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;

import java.util.Base64;
import java.util.UUID;

import javax.validation.Valid;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edge.domain.service.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.folio.edge.client.ModInnReachFeignClient;
import org.folio.edge.domain.dto.InnReachHeadersHolder;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.dto.modinnreach.CentralServerAuthenticationRequest;
import org.folio.edge.domain.exception.EdgeServiceException;
import org.folio.edge.dto.AccessTokenResponse;

@RequiredArgsConstructor
@Service
@Slf4j
@Validated
public class AuthenticationServiceImpl implements AuthenticationService {

  private static final String AUTHENTICATION_TOKEN_KEY_SECRET_DELIMITER = ":";
  private static final int KEY_POSITION_IN_TOKEN = 0;
  private static final int SECRET_POSITION_IN_TOKEN = 1;

  private final ModInnReachFeignClient modInnReachFeignClient;
  private final AccessTokenService<JwtAccessToken, Jwt> accessTokenService;

  @Override
  public AccessTokenResponse authenticate(@Valid InnReachHeadersHolder innReachHeadersHolder) {
    // todo - validate xToCode

    var authenticationRequest = buildCentralServerAuthenticationRequest(innReachHeadersHolder);
    var authenticationResult = modInnReachFeignClient.authenticateCentralServer(authenticationRequest);

    if (!authenticationResult.getStatusCode().is2xxSuccessful()) {
      log.debug("Authentication failed with status: {}", authenticationResult.getStatusCodeValue());
      throw new EdgeServiceException("Authentication failed");
    }

    var jwtAccessToken = accessTokenService.generateAccessToken();

    return new AccessTokenResponse()
      .accessToken(jwtAccessToken.getToken())
      .tokenType(BEARER_AUTH_SCHEME)
      .expiresIn(jwtAccessToken.getExpiresIn());
  }

  private CentralServerAuthenticationRequest buildCentralServerAuthenticationRequest(InnReachHeadersHolder innReachHeadersHolder) {
    var decodedAuthorizationHeader = decodeAuthorizationHeader(innReachHeadersHolder.getAuthorization());
    var keySecretArray = decodedAuthorizationHeader.split(AUTHENTICATION_TOKEN_KEY_SECRET_DELIMITER);

    return CentralServerAuthenticationRequest.builder()
      .localServerCode(innReachHeadersHolder.getXFromCode()) // todo - verify
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
