package org.folio.edge.domain.service;

public interface AccessTokenService<T, R> {

  T generateAccessToken(String xFromCode);

  R verifyAccessToken(T accessToken);
}
