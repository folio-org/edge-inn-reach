package org.folio.edge.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/v2/oauth2")
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  @Override
  @PostMapping("/token")
  public ResponseEntity<AccessTokenResponse> getToken(@NotNull @Pattern(regexp = "client_credentials") @Valid String grantType,
                                                      @NotNull @Pattern(regexp = "innreach_tp") @Valid String scope,
                                                      String xFromCode, Integer xRequestCreationTime, String xToCode,
                                                      String xOkapiTenant, String xOkapiToken, String authorization) {
    var authenticationRequest = buildInnReachHeadersHolder(authorization, xFromCode,
      xRequestCreationTime, xToCode, xOkapiTenant, xOkapiToken);

    var accessTokenResponse = authenticationService.authenticate(authenticationRequest);

    return ResponseEntity.ok(accessTokenResponse);
  }

  private AuthenticationParams buildInnReachHeadersHolder(String authorization, String xFromCode, Integer xRequestCreationTime,
                                                          String xToCode, String okapiTenant, String okapiToken) {
    return AuthenticationParams
      .builder()
      .authorization(authorization)
      .xFromCode(xFromCode)
      .xRequestCreationTime(xRequestCreationTime)
      .xToCode(xToCode)
      .okapiTenant(okapiTenant)
      .okapiToken(okapiToken)
      .build();
  }
}
