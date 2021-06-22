package org.folio.edge.domain.service;

import javax.validation.Valid;

import org.folio.edge.domain.dto.InnReachHeadersHolder;
import org.folio.edge.dto.AccessTokenResponse;

public interface AuthenticationService {

  AccessTokenResponse authenticate(@Valid InnReachHeadersHolder innReachHeadersHolder);
}
