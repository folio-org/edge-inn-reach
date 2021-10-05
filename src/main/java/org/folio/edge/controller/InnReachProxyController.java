package org.folio.edge.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.folio.edge.client.InnReachClient;
import org.folio.edge.domain.InnReachRequestBuilder;

@Log4j2
@RestController
@RequestMapping("/innreach/v2/**")
@RequiredArgsConstructor
public class InnReachProxyController {

  private final InnReachRequestBuilder innReachRequestBuilder;
  private final InnReachClient innReachClient;

  @GetMapping
  public ResponseEntity<?> handleGETRequest(HttpServletRequest request) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);
    return innReachClient.getCall(innReachRequest.getRequestUrl(), innReachRequest.getHeaders());
  }

  @PostMapping
  public ResponseEntity<?> handlePOSTRequest(HttpServletRequest request) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);
    return innReachClient.postCall(innReachRequest.getRequestUrl(), innReachRequest.getRequestBody(), innReachRequest.getHeaders());
  }

  @PutMapping
  public ResponseEntity<?> handlePUTRequest(HttpServletRequest request) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);
    return innReachClient.putCall(innReachRequest.getRequestUrl(), innReachRequest.getRequestBody(), innReachRequest.getHeaders());
  }

  @DeleteMapping
  public ResponseEntity<?> handleDELETERequest(HttpServletRequest request) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);
    innReachClient.deleteCall(innReachRequest.getRequestUrl(), innReachRequest.getHeaders());
    return ResponseEntity.noContent().build();
  }

}
