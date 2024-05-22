package org.folio.ed.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.edge.core.utils.ApiKeyUtils;
import org.folio.edgecommonspring.client.EdgeFeignClientProperties;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.spring.model.UserToken;
import org.folio.spring.service.SystemUserService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ed.utils.EntityUtils.createDcbTransaction;
import static org.folio.ed.utils.EntityUtils.createTransactionStatus;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DcbEdgeRequestHandlingTest {

  private final String TENANT = "test_tenant", USERNAME = "user", TOKEN = "This is totally a real test token!", TRANSACTION_ID = "123";
  protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private EdgeFeignClientProperties properties;
  @MockBean
  private SystemUserService systemUserService;
  private MockWebServer mockDcbServer;

  @BeforeEach
  void setUp() throws IOException {
    mockDcbServer = new MockWebServer();
    mockDcbServer.start();
    var url = "http://localhost:" + mockDcbServer.getPort();
    ReflectionTestUtils.setField(properties, "okapiUrl", url);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockDcbServer.shutdown();
  }

  @Test
  void shouldConvertApiKeyToHeadersForGet() throws Exception {
    // Given
    var apiKey = ApiKeyUtils.generateApiKey(10, TENANT, USERNAME);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(TOKEN);

    // When we make a valid request to mod-dcb with the API key set
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var getResponse = mockMvc.perform(get("/dcbService/transactions/{transactionId}/status?apiKey={apiKey}", TRANSACTION_ID, apiKey)
      .contentType(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();
    // Then the outgoing response from the edge API should contain the Okapi auth headers and the response body should
    // match mod-dcb response
    var headers = mockDcbServer.takeRequest().getHeaders();
    assertThat(headers.get(XOkapiHeaders.TENANT)).isEqualTo(TENANT);
    assertThat(headers.get(XOkapiHeaders.TOKEN)).isEqualTo(TOKEN);
    assertThat(headers.get(XOkapiHeaders.USER_ID)).isNull();
    assertThat(getResponse.getContentAsString()).isEqualTo(responseBody);
  }

  @Test
  void shouldConvertApiKeyToHeadersForPost() throws Exception {
    // Given
    var apiKey = ApiKeyUtils.generateApiKey(10, TENANT, USERNAME);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(TOKEN);
    // When we make a valid request to mod-dcb with the API key set
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(201)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var postResponse = mockMvc.perform(post("/dcbService/transactions/{transactionId}?apiKey={apiKey}", TRANSACTION_ID, apiKey)
        .content(asJsonString(createDcbTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // Then the outgoing response from the edge API should contain the Okapi auth headers and the response body should
    // match mod-dcb response
    var headers = mockDcbServer.takeRequest().getHeaders();
    assertThat(headers.get(XOkapiHeaders.TENANT)).isEqualTo(TENANT);
    assertThat(headers.get(XOkapiHeaders.TOKEN)).isEqualTo(TOKEN);
    assertThat(headers.get(XOkapiHeaders.USER_ID)).isNull();
    assertThat(postResponse.getContentAsString()).isEqualTo(responseBody);
  }

  @Test
  void shouldThrowErrorForInvalidRole() throws Exception {
    // Given
    var apiKey = ApiKeyUtils.generateApiKey(10, TENANT, USERNAME);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(TOKEN);
    var dcbTransaction = createDcbTransaction();
    dcbTransaction.setRole(null);
    // When we make a valid request to mod-dcb with the API key set
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(201)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var postResponse = mockMvc.perform(post("/dcbService/transactions/{transactionId}?apiKey={apiKey}", TRANSACTION_ID, apiKey)
        .content(asJsonString(dcbTransaction + "\"role\" : \"\test\""))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    assertThat(postResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void shouldThrowErrorForInvalidUUID() throws Exception {
    // Given
    var apiKey = ApiKeyUtils.generateApiKey(10, TENANT, USERNAME);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(TOKEN);
    var dcbTransaction = createDcbTransaction();
    dcbTransaction.getItem().setId("123");
    // When we make a valid request to mod-dcb with the API key set
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(201)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var postResponse = mockMvc.perform(post("/dcbService/transactions/{transactionId}?apiKey={apiKey}", TRANSACTION_ID, apiKey)
        .content(asJsonString(dcbTransaction ))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    assertThat(postResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void shouldThrowErrorForFeignException() throws Exception {
    // Given
    var apiKey = ApiKeyUtils.generateApiKey(10, TENANT, USERNAME);
    setUpMockAuthnClient(TOKEN);
    var dcbTransaction = createDcbTransaction();
    // When we make a valid request to mod-dcb with the API key set
    org.folio.ed.domain.dto.Errors errors = org.folio.ed.domain.dto.Errors.builder()
      .errors(List.of(org.folio.ed.domain.dto.Error.builder()
        .message("Unable to find Dcb transaction")
        .build()))
    .build();
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(404)
      .setBody(asJsonString(errors)));
    var postResponse = mockMvc.perform(post("/dcbService/transactions/{transactionId}?apiKey={apiKey}", TRANSACTION_ID, apiKey)
        .content(asJsonString(dcbTransaction))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();
    // As dcb will return Error message of type Errors, trying to assert the same here
    var errorMessage = new ObjectMapper().readValue(postResponse.getContentAsString(), org.folio.ed.domain.dto.Errors.class);

    assertThat(postResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(errorMessage.getErrors().get(0).getMessage()).contains("Unable to find Dcb transaction");
  }

  @Test
  void shouldConvertApiKeyToHeadersForPut() throws Exception {
    // Given
    String tenant = "test_tenant",
      username = "user",
      token = "This is totally a real test token!";
    var transactionId = "123";
    var apiKey = ApiKeyUtils.generateApiKey(10, tenant, username);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(token);

    // When we make a valid request to mod-dcb with the API key set
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(204)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var putResponse = mockMvc.perform(put("/dcbService/transactions/{transactionId}/status?apiKey={apiKey}", transactionId, apiKey)
        .content(asJsonString(createTransactionStatus(TransactionStatus.StatusEnum.AWAITING_PICKUP)))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // Then the outgoing response from the edge API should contain the Okapi auth headers and the response body should
    // match mod-dcb response
    var headers = mockDcbServer.takeRequest().getHeaders();
    assertThat(headers.get(XOkapiHeaders.TENANT)).isEqualTo(tenant);
    assertThat(headers.get(XOkapiHeaders.TOKEN)).isEqualTo(token);
    assertThat(headers.get(XOkapiHeaders.USER_ID)).isNull();
    assertThat(putResponse.getContentAsString()).isEqualTo(responseBody);
  }

  @Test
  void shouldConvertApiKeyToHeadersForGetTransactionStatusList() throws Exception {
    // Given
    String tenant = "test_tenant",
      username = "user",
      token = "This is totally a real test token!";
    var apiKey = ApiKeyUtils.generateApiKey(10, tenant, username);
    var responseBody = ""; // Arbitrary string. We don't care about the actual content and an empty string is easy
    setUpMockAuthnClient(token);

    // When we make a valid request to mod-dcb with the API key set
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(204)
      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setBody(responseBody));
    var startDate = OffsetDateTime.now(ZoneOffset.UTC);
    var endDate = OffsetDateTime.now(ZoneOffset.UTC);
    var putResponse = mockMvc.perform(get("/dcbService/transactions/status?apiKey={apiKey}", apiKey)
        .param("fromDate", String.valueOf(startDate))
        .param("toDate", String.valueOf(endDate))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // Then the outgoing response from the edge API should contain the Okapi auth headers and the response body should
    // match mod-dcb response
    var headers = mockDcbServer.takeRequest().getHeaders();
    assertThat(headers.get(XOkapiHeaders.TENANT)).isEqualTo(tenant);
    assertThat(headers.get(XOkapiHeaders.TOKEN)).isEqualTo(token);
    assertThat(headers.get(XOkapiHeaders.USER_ID)).isNull();
    assertThat(putResponse.getContentAsString()).isEqualTo(responseBody);
  }

  @Test
  void shouldReturnClientErrors() throws Exception {
    // Given
    var apiKey = ApiKeyUtils.generateApiKey(10, TENANT, USERNAME);
    var dcbResponseCode = HttpStatus.I_AM_A_TEAPOT.value(); // Arbitrary HTTP error status code
    var dcbResponseBody = "I'm a teapot, not an dcb transaction!";
    setUpMockAuthnClient(TOKEN);

    // When mod-dcb responds with an error
    mockDcbServer.enqueue(new MockResponse()
      .setResponseCode(dcbResponseCode)
      .setBody(dcbResponseBody));
    var response = mockMvc.perform(get("/dcbService/transactions/{transactionId}/status?apiKey={apiKey}", TRANSACTION_ID, apiKey)
        .contentType(MediaType.APPLICATION_JSON))
      .andReturn()
      .getResponse();

    // If mod-dcb doesn't return the error of type errors, then we edge-dcb will throw INTERNAL_SERVER_ERROR
    assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(response.getContentAsString()).contains(dcbResponseBody);
  }

  private void setUpMockAuthnClient(String token) {
    when(systemUserService.authSystemUser(any(), any(), any()))
      .thenReturn(new UserToken(token, Instant.now().plus(1, TimeUnit.HOURS.toChronoUnit())));
  }

  @SneakyThrows
  public static String asJsonString(Object value) {
    return OBJECT_MAPPER.writeValueAsString(value);
  }
}
