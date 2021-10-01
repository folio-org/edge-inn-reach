package org.folio.edge.domain;

import org.folio.edge.domain.dto.InnReachRequest;
import org.folio.edge.domain.exception.EdgeServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URI;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

  @BeforeEach
  private void setup() throws IOException {
    when(httpServletRequest.getRequestURI()).thenReturn(INN_REACH_URI_PREFIX + "/resource/subresource");
    when(httpServletRequest.getInputStream()).thenReturn(servletInputStream);
  }

  @Test
  public void returnInnReachRequestWithURLandRequestBody() throws IOException {
    when(servletInputStream.readAllBytes()).thenReturn(new byte[] {});

    var innReachRequest = innReachRequestBuilder.buildInnReachRequest(httpServletRequest);

    assertNotNull(innReachRequest);
    assertEquals(URI.create(okapiUrl + INN_REACH_D2IR_URL_PREFIX + "/resource/subresource"), innReachRequest.getRequestUrl());
    assertEquals(EMPTY, innReachRequest.getRequestBody());
  }

  @Test
  public void throwException_when_cantReadRequestBodyAsString() throws IOException {
    assertThrows(EdgeServiceException.class, () -> innReachRequestBuilder.buildInnReachRequest(httpServletRequest));
  }
}
