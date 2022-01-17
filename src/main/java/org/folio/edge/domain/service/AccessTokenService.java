package org.folio.edge.domain.service;

import java.util.UUID;

public interface AccessTokenService<T, R> {

  T generateAccessToken(UUID localServerKey);

  R verifyAccessToken(T accessToken);
}
