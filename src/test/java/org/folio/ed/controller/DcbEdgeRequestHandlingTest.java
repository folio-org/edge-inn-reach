package org.folio.ed.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.folio.edge.core.utils.ApiKeyUtils;
import org.folio.edgecommonspring.client.EnrichUrlClient;
import org.folio.edgecommonspring.client.AuthnClient;
import org.folio.spring.integration.XOkapiHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DcbEdgeRequestHandlingTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private EnrichUrlClient enrichUrlClient;
  @MockBean
  private AuthnClient authnClient;
  private MockWebServer mockDcbServer;

  @BeforeEach
  void setUp() throws IOException {
    mockDcbServer = new MockWebServer();
    mockDcbServer.start();
    ReflectionTestUtils.setField(enrichUrlClient, "okapiUrl", "http://localhost:" + mockDcbServer.getPort());
  }

  @AfterEach
  void tearDown() throws IOException {
    mockDcbServer.shutdown();
  }

  @Test
  void shouldConvertApiKeyToHeaders() throws Exception {
    // Given
    String tenant = "test_tenant",
      username = "user",
      token = "This is totally a real test token!";
    var transactionId = "123";
    var apiKey = ApiKeyUtils.generateApiKey(10, tenant, username);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(tenant, token);

    // When we make a valid request to mod-dcb with the API key set
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var response = mockMvc.perform(get("/dcbService/transactions/{transactionId}/status?apiKey={apiKey}", transactionId, apiKey)
      .contentType(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // Then the outgoing response from the edge API should contain the Okapi auth headers and the response body should
    // match mod-dcb response
    var headers = mockDcbServer.takeRequest().getHeaders();
    assertThat(headers.get(XOkapiHeaders.TENANT)).isEqualTo(tenant);
    assertThat(headers.get(XOkapiHeaders.TOKEN)).isEqualTo(token);
    assertThat(headers.get(XOkapiHeaders.USER_ID)).isNull();
    assertThat(response.getContentAsString()).isEqualTo(responseBody);
  }

  @Test
  void shouldReturnClientErrors() throws Exception {
    // Given
    String tenant = "test_tenant",
      username = "user",
      token = "This is totally a real token. For real!";
    var transactionId = "123";
    var apiKey = ApiKeyUtils.generateApiKey(10, tenant, username);
    var dcbResponseCode = HttpStatus.I_AM_A_TEAPOT.value(); // Arbitrary HTTP error status code
    var dcbResponseBody = "I'm a teapot, not an dcb transaction!";
    setUpMockAuthnClient(tenant, token);

    // When mod-dcb responds with an error
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(dcbResponseCode)
      .setBody(dcbResponseBody));
    var response = mockMvc.perform(get("/dcbService/transactions/{transactionId}/status?apiKey={apiKey}", transactionId, apiKey)
        .contentType(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // Then the edge API response should contain the error message from mod-dcb
    assertThat(response.getStatus()).isEqualTo(dcbResponseCode);
    assertThat(response.getContentAsString()).isEqualTo(dcbResponseBody);
  }

  private void setUpMockAuthnClient(String tenant, String token) {
    var responseHeaders = new HttpHeaders() {{
      add(XOkapiHeaders.TENANT, tenant);
      add(XOkapiHeaders.TOKEN, token);
    }};
    when(authnClient.getApiKey(any(), eq(tenant)))
      .thenReturn(new ResponseEntity<>(null, responseHeaders, HttpStatus.OK));
  }
}
