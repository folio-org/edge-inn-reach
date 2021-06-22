package org.folio.edge.fixture;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;
import static org.folio.edge.util.TestUtil.randomFiveCharacterCode;

import java.util.Base64;
import java.util.UUID;

import org.springframework.http.HttpHeaders;

import org.folio.edge.domain.dto.InnReachHeadersHolder;
import org.folio.edge.external.InnReachHttpHeaders;

public class InnReachFixture {

  public static InnReachHeadersHolder createInnReachHeadersHolder() {
    return InnReachHeadersHolder
      .builder()
      .authorization(createAuthenticationToken())
      .xRequestCreationTime(Integer.MAX_VALUE)
      .xFromCode(randomFiveCharacterCode())
      .xToCode(randomFiveCharacterCode())
      .build();
  }

  public static HttpHeaders createInnReachHttpHeaders() {
    var httpHeaders = new HttpHeaders();
    httpHeaders.add(InnReachHttpHeaders.X_FROM_CODE, randomFiveCharacterCode());
    httpHeaders.add(InnReachHttpHeaders.X_TO_CODE, randomFiveCharacterCode());
    httpHeaders.add(InnReachHttpHeaders.X_REQUEST_CREATION_TIME, String.valueOf(Integer.MAX_VALUE));
    httpHeaders.add(HttpHeaders.AUTHORIZATION, String.format("%s %s", BASIC_AUTH_SCHEME, createAuthenticationToken()));

    return httpHeaders;
  }

  private static String createAuthenticationToken() {
    return Base64.getEncoder().encodeToString(String.format("%s:%s", UUID.randomUUID(), UUID.randomUUID()).getBytes());
  }
}
