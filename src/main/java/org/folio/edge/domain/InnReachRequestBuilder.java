package org.folio.edge.domain;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.folio.edgecommonspring.client.EdgeFeignClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.edge.domain.dto.InnReachRequest;
import org.folio.edge.domain.exception.EdgeServiceException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Log4j2
@Component
@RequiredArgsConstructor
public class InnReachRequestBuilder {

  private static final String INN_REACH_URI_PREFIX = "/innreach/v2";
  private static final String INN_REACH_D2IR_URL_PREFIX = "/inn-reach/d2ir";

  private final EdgeFeignClientProperties properties;

  @Deprecated
  @Value("${okapi_url:#{null}}")
  private String okapiUrl;


  public InnReachRequest buildInnReachRequest(HttpServletRequest request) {
    log.debug("Build inn-reach request :: parameter request : {}", request.toString());
    return InnReachRequest.builder()
      .requestUrl(buildRequestUrl(request))
      .requestBody(requestBodyAsString(request))
      .headers(collectHeaders(request))
      .build();
  }

  private URI buildRequestUrl(HttpServletRequest request) {
    log.debug("Build Request URL :: parameter request : {} ", request);
    var requestURI = request.getRequestURI().replaceAll(INN_REACH_URI_PREFIX, StringUtils.EMPTY);

    String okapiUrlToUse = okapiUrl;
    if (isNotBlank(okapiUrlToUse)) {
      log.warn("deprecated property okapi_url is used. Please use folio.client.okapiUrl instead.");
    } else {
      okapiUrlToUse = properties.getOkapiUrl();
    }

    return URI.create(okapiUrlToUse + INN_REACH_D2IR_URL_PREFIX + requestURI);
  }

  private String requestBodyAsString(HttpServletRequest request) {
    try {
      return new String(request.getInputStream().readAllBytes());
    } catch (Exception e) {
      log.error("Can't read request body as String", e);
      throw new EdgeServiceException("Can't parse request body");
    }
  }

  private Map<String, String> collectHeaders(HttpServletRequest request) {
    var headers = new HashMap<String, String>();

    request.getHeaderNames()
      .asIterator()
      .forEachRemaining(headerName -> headers.put(headerName, request.getHeader(headerName)));

    return headers;
  }
}
