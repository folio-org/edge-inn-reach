package org.folio.edge.client;

import static org.folio.spring.integration.XOkapiHeaders.TENANT;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.folio.edge.config.OkapiFeignClientConfig;
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;

@FeignClient(value = "authn", configuration = OkapiFeignClientConfig.class)
public interface AuthnClient {

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<String> getApiKey(@RequestBody ConnectionSystemParameters connectionSystemParameters,
                                   @RequestHeader(TENANT) String tenantId);

}
