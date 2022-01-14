package org.folio.edge.client;

import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.folio.edge.config.OkapiFeignClientConfig;
import org.folio.edge.domain.dto.modinnreach.LocalServerCredentials;

@FeignClient(value = "inn-reach", configuration = OkapiFeignClientConfig.class)
public interface InnReachAuthClient {

  @PostMapping("/authentication")
  ResponseEntity<Object> authenticateCentralServer(@RequestBody LocalServerCredentials localServerCredentials,
                                                   @RequestHeader(TENANT) String okapiTenant,
                                                   @RequestHeader(TOKEN) String okapiToken);
}
