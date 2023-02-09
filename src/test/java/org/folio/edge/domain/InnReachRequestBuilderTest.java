package org.folio.edge.domain;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import org.folio.edge.domain.exception.EdgeServiceException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class InnReachRequestBuilderTest {

  private static final String INN_REACH_URI_PREFIX = "/innreach/v2";
  private static final String INN_REACH_D2IR_URL_PREFIX = "/inn-reach/d2ir";

  @Value("${okapi_url}")
  private String okapiUrl;

  @Autowired
  private InnReachRequestBuilder innReachRequestBuilder;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private ServletInputStream servletInputStream;

  @Test
  public void returnInnReachRequestWithURLandRequestBody() throws IOException {
    var httpServletRequest = getMockHttpServletRequest();

    when(servletInputStream.readAllBytes()).thenReturn(new byte[] {});

    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(httpServletRequest);

    assertNotNull(innReachRequest);
    assertNotNull(innReachRequest.getHeaders());

    assertFalse(innReachRequest.getHeaders().isEmpty());

    assertEquals(URI.create(okapiUrl + INN_REACH_D2IR_URL_PREFIX + "/resource/subresource"), innReachRequest.getRequestUrl());
    assertEquals(EMPTY, innReachRequest.getRequestBody());
  }

  @Test
  public void throwException_when_cantReadRequestBodyAsString() {
    var httpServletRequest = getMockHttpServletRequest();
    assertThrows(EdgeServiceException.class, () -> innReachRequestBuilder.buildInnReachRequest(httpServletRequest));
  }

  private HttpServletRequestWrapper getMockHttpServletRequest() {
    return new HttpServletRequestWrapper(httpServletRequest) {

      private final Map<String, String> headers = new HashMap<>();

      @Override
      public String getRequestURI() {
        return INN_REACH_URI_PREFIX + "/resource/subresource";
      }

      @Override
      public ServletInputStream getInputStream() {
        return servletInputStream;
      }

      @Override
      public Enumeration<String> getHeaderNames() {
        headers.put("x-code-from", "fli01");
        return Collections.enumeration(headers.keySet());
      }

      @Override
      public String getHeader(String name) {
        return headers.get(name);
      }
    };
  }
}
