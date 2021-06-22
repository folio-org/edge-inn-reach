package org.folio.edge.fixture;

import org.folio.edge.domain.dto.JwtAccessToken;

public class JwtTokenFixture {

  public static JwtAccessToken createRandomJwtAccessToken(String jwtToken) {
    return JwtAccessToken
      .builder()
      .token(jwtToken)
      .expiresIn(599)
      .build();
  }
}
