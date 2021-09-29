package org.folio.edge.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.folio.edge.client.InnReachClient;
import org.folio.edge.domain.InnReachRequestBuilder;
import org.folio.spring.integration.XOkapiHeaders;

@Log4j2
@RestController
@RequestMapping("/innreach/v2/**")
@RequiredArgsConstructor
public class InnReachProxyController {

  private final InnReachRequestBuilder innReachRequestBuilder;
  private final InnReachClient innReachClient;

  @GetMapping
  public ResponseEntity<?> handleGETRequest(HttpServletRequest request,
                                            @RequestHeader(XOkapiHeaders.TOKEN) String okapiToken,
                                            @RequestHeader(XOkapiHeaders.TENANT) String okapiTenant) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);

    return innReachClient.getCall(innReachRequest.getRequestUrl(), okapiTenant, okapiToken);
  }

  @PostMapping
  public ResponseEntity<?> handlePOSTRequest(HttpServletRequest request,
                                             @RequestHeader(XOkapiHeaders.TOKEN) String okapiToken,
                                             @RequestHeader(XOkapiHeaders.TENANT) String okapiTenant) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);

    return innReachClient.postCall(innReachRequest.getRequestUrl(), innReachRequest.getRequestBody(), okapiTenant, okapiToken);
  }

  @PutMapping
  public ResponseEntity<?> handlePUTRequest(HttpServletRequest request,
                                            @RequestHeader(XOkapiHeaders.TOKEN) String okapiToken,
                                            @RequestHeader(XOkapiHeaders.TENANT) String okapiTenant) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);

    return innReachClient.putCall(innReachRequest.getRequestUrl(), innReachRequest.getRequestBody(), okapiTenant, okapiToken);
  }

  @DeleteMapping
  public ResponseEntity<?> handleDELETERequest(HttpServletRequest request,
                                               @RequestHeader(XOkapiHeaders.TOKEN) String okapiToken,
                                               @RequestHeader(XOkapiHeaders.TENANT) String okapiTenant) {
    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(request);
    innReachClient.deleteCall(innReachRequest.getRequestUrl(), okapiTenant, okapiToken);

    return ResponseEntity.noContent().build();
  }

}
