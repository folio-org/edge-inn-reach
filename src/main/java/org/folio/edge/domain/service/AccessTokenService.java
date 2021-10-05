package org.folio.edge.domain.service;

public interface AccessTokenService<T, R> {

  T generateAccessToken(String xFromCode, String xToCode);

  R verifyAccessToken(T accessToken);
}
