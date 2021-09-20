package org.folio.edge.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.folio.edge.config.feign.FolioFeignClientConfig;
import org.folio.edge.aspect.annotation.WithinSystemUserExecutionContext;
import org.folio.edge.domain.dto.modinnreach.CentralServerAuthenticationRequest;

@FeignClient(value = "inn-reach", configuration = FolioFeignClientConfig.class)
public interface ModInnReachFeignClient {

  @WithinSystemUserExecutionContext
  @PostMapping("/authentication")
  ResponseEntity<Object> authenticateCentralServer(CentralServerAuthenticationRequest centralServerAuthenticationRequest);
}
