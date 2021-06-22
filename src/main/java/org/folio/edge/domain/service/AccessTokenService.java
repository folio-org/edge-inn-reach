package org.folio.edge.domain.service;

public interface AccessTokenService<T, R> {

  T generateAccessToken();

  R verifyAccessToken(T accessToken);
}
