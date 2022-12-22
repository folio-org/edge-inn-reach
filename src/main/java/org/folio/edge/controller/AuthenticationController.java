package org.folio.edge.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.folio.edge.domain.dto.AuthenticationParams;
import org.folio.edge.domain.service.AuthenticationService;
import org.folio.edge.dto.AccessTokenResponse;
import org.folio.edge.rest.resource.AuthenticationApi;

@RequiredArgsConstructor
@RestController
@RequestMapping("/innreach/v2/oauth2")
@Log4j2
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  @Override
  @PostMapping("/token")
  public ResponseEntity<AccessTokenResponse> getToken(@NotNull @Pattern(regexp = "client_credentials") @Valid String grantType,
                                                      @NotNull @Pattern(regexp = "innreach_tp") @Valid String scope,
                                                      String xOkapiTenant, String xOkapiToken, String authorization) {
    log.debug("Get token for authentication :: parameter grantType : {}, scope : {}, " +
      "xOkapiTenant : {}, xOkapiToken : {}, authorization : {} ", grantType, scope, xOkapiTenant,
      xOkapiToken, authorization);
    var authenticationRequest = buildInnReachHeadersHolder(authorization, xOkapiTenant, xOkapiToken);

    var accessTokenResponse = authenticationService.authenticate(authenticationRequest);

    log.info("Access token retrieved successfully.");
    return ResponseEntity.ok(accessTokenResponse);
  }

  private AuthenticationParams buildInnReachHeadersHolder(String authorization, String okapiTenant, String okapiToken) {
    log.debug("Build inn-reach headers :: parameter authorization : {}, okapiTenant : {}, okapiToken : {}",authorization,
    okapiTenant, okapiToken);
    return AuthenticationParams
      .builder()
      .authorization(authorization)
      .okapiTenant(okapiTenant)
      .okapiToken(okapiToken)
      .build();
  }
}
