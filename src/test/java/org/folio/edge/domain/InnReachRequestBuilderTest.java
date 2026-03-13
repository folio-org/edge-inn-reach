package org.folio.edge.domain;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.folio.edge.domain.exception.EdgeServiceException;
import org.folio.edgecommonspring.client.EdgeClientProperties;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class InnReachRequestBuilderTest {

  private static final String INN_REACH_URI_PREFIX = "/innreach/v2";
  private static final String INN_REACH_D2IR_URL_PREFIX = "/inn-reach/d2ir";

  @Autowired
  private EdgeClientProperties properties;

  @Autowired
  private InnReachRequestBuilder innReachRequestBuilder;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private ServletInputStream servletInputStream;

  @Test
  @Order(1)
  public void returnInnReachRequestWithURLandRequestBody() throws IOException {
    var httpServletRequest = getMockHttpServletRequest();

    when(servletInputStream.readAllBytes()).thenReturn(new byte[] {});

    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(httpServletRequest);

    assertNotNull(innReachRequest);
    assertNotNull(innReachRequest.getHeaders());
    assertFalse(innReachRequest.getHeaders().isEmpty());
    assertEquals(URI.create(properties.getOkapiUrl() + INN_REACH_D2IR_URL_PREFIX + "/resource/subresource"), innReachRequest.getRequestUrl());
    assertEquals(EMPTY, innReachRequest.getRequestBody());
  }

  @Test
  @Order(2)
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
