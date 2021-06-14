package org.folio.edge.client;

import org.folio.edge.config.OkapiFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.folio.edge.domain.dto.modinnreach.CentralServerAuthenticationRequest;

@FeignClient(value = "inn-reach", configuration = OkapiFeignClientConfig.class)
public interface ModInnReachFeignClient {

  @PostMapping("/authentication")
  ResponseEntity<Object> authenticateCentralServer(CentralServerAuthenticationRequest centralServerAuthenticationRequest);
}
