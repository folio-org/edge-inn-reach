package org.folio.edge.client;

import org.folio.edge.config.feign.FolioFeignClientConfig;
import org.folio.edge.aspect.annotation.WithinTenantExecutionContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.folio.edge.domain.dto.systemuser.UserCredentials;

@FeignClient(value = "authn", configuration = FolioFeignClientConfig.class)
public interface AuthnClient {

  @WithinTenantExecutionContext
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<String> getApiKey(@RequestBody UserCredentials credentials);

  @WithinTenantExecutionContext
  @PostMapping(value = "/credentials", consumes = MediaType.APPLICATION_JSON_VALUE)
  void saveCredentials(@RequestBody UserCredentials credentials);
}
