package org.folio.edge.client;

import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.folio.edge.config.InnReachClientConfig;

@FeignClient(value = "inn-reach/d2ir", configuration = InnReachClientConfig.class)
public interface InnReachClient {

  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> getCall(URI modInnReachURI,
                            @RequestHeader(TENANT) String okapiTenant,
                            @RequestHeader(TOKEN) String okapiToken);

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> postCall(URI modInnReachURI,
                             @RequestBody String requestBody,
                             @RequestHeader(TENANT) String okapiTenant,
                             @RequestHeader(TOKEN) String okapiToken);

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> putCall(URI modInnReachURI,
                            @RequestBody String requestBody,
                            @RequestHeader(TENANT) String okapiTenant,
                            @RequestHeader(TOKEN) String okapiToken);

  @DeleteMapping
  void deleteCall(URI modInnReachURI,
                  @RequestHeader(TENANT) String okapiTenant,
                  @RequestHeader(TOKEN) String okapiToken);
}
