package org.folio.edge.client;

import java.net.URI;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(contentType = MediaType.APPLICATION_JSON_VALUE)
public interface InnReachClient {

  @GetExchange
  ResponseEntity<?> getCall(URI modInnReachURI, @RequestHeader Map<String, String> headers);

  @PostExchange
  ResponseEntity<?> postCall(URI modInnReachURI, @RequestBody String requestBody, @RequestHeader Map<String, String> headers);

  @PutExchange
  ResponseEntity<?> putCall(URI modInnReachURI, @RequestBody String requestBody, @RequestHeader Map<String, String> headers);

  @DeleteExchange
  void deleteCall(URI modInnReachURI, @RequestHeader Map<String, String> headers);
}
