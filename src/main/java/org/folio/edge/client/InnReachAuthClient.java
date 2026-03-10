package org.folio.edge.client;

import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import org.folio.edge.domain.dto.modinnreach.LocalServerCredentials;

@HttpExchange(url = "inn-reach")
public interface InnReachAuthClient {

  @PostExchange("/authentication")
  ResponseEntity<Object> authenticateCentralServer(@RequestBody LocalServerCredentials localServerCredentials,
    @RequestHeader(TENANT) String okapiTenant,
    @RequestHeader(TOKEN) String okapiToken);
}
