package org.folio.edge.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.folio.edge.domain.dto.AccessTokenRequest;
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
                                                      String xFromCode,
                                                      Integer xRequestCreationTime,
                                                      String xToCode,
                                                      String authorization) {
    var accessTokenRequest = buildAccessTokenRequest(authorization, xFromCode, xRequestCreationTime, xToCode);

    var accessTokenResponse = authenticationService.getAccessToken(accessTokenRequest);

    return ResponseEntity.ok(accessTokenResponse);
  }

  private AccessTokenRequest buildAccessTokenRequest(String authorization, String xFromCode,
      Integer xRequestCreationTime, String xToCode) {
    return AccessTokenRequest
      .builder()
      .authorization(authorization)
      .xFromCode(xFromCode)
      .xRequestCreationTime(xRequestCreationTime)
      .xToCode(xToCode)
      .build();
  }
}
