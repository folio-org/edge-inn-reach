package org.folio.edge.client;

import java.net.URI;
import java.util.Map;

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
  ResponseEntity<?> getCall(URI modInnReachURI, @RequestHeader Map<String, String> headers);

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> postCall(URI modInnReachURI, @RequestBody String requestBody, @RequestHeader Map<String, String> headers);

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> putCall(URI modInnReachURI, @RequestBody String requestBody, @RequestHeader Map<String, String> headers);

  @DeleteMapping
  void deleteCall(URI modInnReachURI, @RequestHeader Map<String, String> headers);
}
