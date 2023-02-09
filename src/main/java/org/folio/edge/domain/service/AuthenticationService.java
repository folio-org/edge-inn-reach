package org.folio.edge.domain.service;

import jakarta.validation.Valid;

import org.folio.edge.domain.dto.AuthenticationParams;
import org.folio.edge.dto.AccessTokenResponse;

public interface AuthenticationService {

  AccessTokenResponse authenticate(@Valid AuthenticationParams authenticationParams);
}
