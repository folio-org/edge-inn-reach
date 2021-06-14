package org.folio.edge.domain.service;

import org.folio.edge.domain.dto.AccessTokenRequest;
import org.folio.edge.dto.AccessTokenResponse;

import javax.validation.Valid;

public interface AuthenticationService {

  AccessTokenResponse getAccessToken(@Valid AccessTokenRequest accessTokenRequest);
}
