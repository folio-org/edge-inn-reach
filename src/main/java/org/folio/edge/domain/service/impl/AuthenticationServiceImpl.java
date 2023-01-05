package org.folio.edge.domain.service.impl;


import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BEARER_AUTH_SCHEME;
import static org.folio.edge.utils.CredentialsUtils.parseBasicAuth;

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
import org.folio.edge.domain.exception.EdgeServiceException;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edge.domain.service.AuthenticationService;
import org.folio.edge.dto.AccessTokenResponse;

@RequiredArgsConstructor
@Service
@Log4j2
@Validated
public class AuthenticationServiceImpl implements AuthenticationService {

  private final InnReachAuthClient innReachAuthClient;
  private final AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;

  @Override
  public AccessTokenResponse authenticate(@Valid AuthenticationParams authParams) {
    log.debug("Authenticate the client by calling the central server :: parameter authParams : {} ", authParams.toString());
    var authenticationRequest = parseBasicAuth(authParams.getAuthorization());

    var authResult = innReachAuthClient.authenticateCentralServer(authenticationRequest,
      authParams.getOkapiTenant(), authParams.getOkapiToken());

    if (!authResult.getStatusCode().is2xxSuccessful()) {
      log.warn("Authentication failed with status: {}", authResult.getStatusCodeValue());
      throw new EdgeServiceException("Authentication failed");
    }
    log.info("Authentication succeeded and generate the access token.");
    var jwtAccessToken = accessTokenService.generateAccessToken(authenticationRequest.getKey());

    log.info("Return the Bearer Token.");
    log.info("The Bearer token is " + jwtAccessToken.getToken());
    return new AccessTokenResponse()
      .accessToken(jwtAccessToken.getToken())
      .tokenType(BEARER_AUTH_SCHEME)
      .expiresIn(jwtAccessToken.getExpiresIn());
  }

}
