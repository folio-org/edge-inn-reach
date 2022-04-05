package org.folio.edge.fixture;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;
import static org.folio.edge.util.TestUtil.randomFiveCharacterCode;

import java.util.Base64;
import java.util.UUID;

import org.springframework.http.HttpHeaders;

import org.folio.edge.domain.dto.AuthenticationParams;
import org.folio.edge.external.InnReachHttpHeaders;

public class InnReachFixture {

  public static final String LOCAL_SERVER_KEY = "5858f9d8-1558-4513-aa25-bad839eb803a";

  public static AuthenticationParams createInnReachHeadersHolder() {
    return AuthenticationParams
      .builder()
      .authorization(createAuthenticationToken())
      .build();
  }

  public static HttpHeaders createInnReachHttpHeaders() {
    var httpHeaders = new HttpHeaders();
    httpHeaders.add(InnReachHttpHeaders.X_FROM_CODE, randomFiveCharacterCode());
    httpHeaders.add(InnReachHttpHeaders.X_TO_CODE, "fli01");
    httpHeaders.add(HttpHeaders.AUTHORIZATION, String.format("%s %s", BASIC_AUTH_SCHEME, createAuthenticationToken()));

    return httpHeaders;
  }

  private static String createAuthenticationToken() {
    return Base64.getEncoder().encodeToString(String.format("%s:%s", UUID.fromString(LOCAL_SERVER_KEY), UUID.randomUUID()).getBytes());
  }
}
