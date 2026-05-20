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

@HttpExchange
public interface InnReachClient {

  @GetExchange
  ResponseEntity<?> getCall(URI modInnReachURI, @RequestHeader Map<String, String> headers);

  @PostExchange(contentType = MediaType.APPLICATION_JSON_VALUE, accept = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> postCall(URI modInnReachURI, @RequestBody String requestBody, @RequestHeader Map<String, String> headers);

  @PutExchange(contentType = MediaType.APPLICATION_JSON_VALUE, accept = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> putCall(URI modInnReachURI, @RequestBody String requestBody, @RequestHeader Map<String, String> headers);

  @DeleteExchange
  void deleteCall(URI modInnReachURI, @RequestHeader Map<String, String> headers);
}
