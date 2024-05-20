package org.folio.edge.domain;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.folio.edge.domain.dto.InnReachRequest;
import org.folio.edge.domain.exception.EdgeServiceException;
import org.folio.edgecommonspring.client.EdgeFeignClientProperties;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InnReachRequestBuilderTest {

  private static final String INN_REACH_URI_PREFIX = "/innreach/v2";
  private static final String INN_REACH_D2IR_URL_PREFIX = "/inn-reach/d2ir";

  @Deprecated
  @Value("${okapi_url:#{null}}")
  private String okapiUrl;

  @Autowired
  private EdgeFeignClientProperties properties;

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

    String okapiUrlToUse = okapiUrl;
    if (isBlank(okapiUrlToUse)) {
      okapiUrlToUse = properties.getOkapiUrl();
    }

    assertEquals(URI.create(okapiUrlToUse + INN_REACH_D2IR_URL_PREFIX + "/resource/subresource"), innReachRequest.getRequestUrl());
    assertEquals(EMPTY, innReachRequest.getRequestBody());
  }

  @Test
  @Order(2)
  public void throwException_when_cantReadRequestBodyAsString() {
    var httpServletRequest = getMockHttpServletRequest();
    assertThrows(EdgeServiceException.class, () -> innReachRequestBuilder.buildInnReachRequest(httpServletRequest));
  }

  @Test
  @Order(3)
  void testUseDeprecatedOkapiUrl() throws IOException {
    when(servletInputStream.readAllBytes()).thenReturn(new byte[] {});

    String deprecatedOkapiUrl = "http://deprecated-url";
    ReflectionTestUtils.setField(innReachRequestBuilder, "okapiUrl", deprecatedOkapiUrl);
    ReflectionTestUtils.setField(properties, "okapiUrl", "http://new-url");

    var httpServletRequest = getMockHttpServletRequest();

    InnReachRequest innReachRequest = innReachRequestBuilder.buildInnReachRequest(httpServletRequest);

    assertNotNull(innReachRequest);
    assertEquals(URI.create(deprecatedOkapiUrl + INN_REACH_D2IR_URL_PREFIX + "/resource/subresource"), innReachRequest.getRequestUrl());
  }

  @Test
  @Order(4)
  void testUsePropertiesOkapiUrl() throws IOException {
    when(servletInputStream.readAllBytes()).thenReturn(new byte[] {});

    ReflectionTestUtils.setField(innReachRequestBuilder, "okapiUrl", null);
    ReflectionTestUtils.setField(properties, "okapiUrl", "http://new-url");

    var httpServletRequest = getMockHttpServletRequest();

    InnReachRequest innReachRequest = innReachRequestBuilder.buildInnReachRequest(httpServletRequest);

    assertNotNull(innReachRequest);
    assertEquals(URI.create("http://new-url" + INN_REACH_D2IR_URL_PREFIX + "/resource/subresource"), innReachRequest.getRequestUrl());
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
