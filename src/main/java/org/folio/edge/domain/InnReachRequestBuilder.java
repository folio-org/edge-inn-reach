package org.folio.edge.domain;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.edge.domain.dto.InnReachRequest;
import org.folio.edge.domain.exception.EdgeServiceException;

@Log4j2
@Component
public class InnReachRequestBuilder {

  private static final String INN_REACH_URI_PREFIX = "/innreach/v2";
  private static final String INN_REACH_D2IR_URL_PREFIX = "/inn-reach/d2ir";

  @Value("${okapi_url}")
  private String okapiUrl;

  public InnReachRequest buildInnReachRequest(HttpServletRequest request) {
    var requestUrl = buildRequestUrl(request);
    var requestBody = requestBodyAsString(request);
    return new InnReachRequest(requestUrl, requestBody);
  }

  private URI buildRequestUrl(HttpServletRequest request) {
    var requestURI = request.getRequestURI().replaceAll(INN_REACH_URI_PREFIX, "");
    return URI.create(okapiUrl + INN_REACH_D2IR_URL_PREFIX + requestURI);
  }

  private String requestBodyAsString(HttpServletRequest request) {
    try {
      return new String(request.getInputStream().readAllBytes());
    } catch (Exception e) {
      log.error("Can't read request body as String", e);
      throw new EdgeServiceException("Can't parse request body");
    }
  }
}
