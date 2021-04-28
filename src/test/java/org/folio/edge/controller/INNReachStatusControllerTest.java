package org.folio.edge.controller;

import org.folio.spring.integration.XOkapiHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")

class INNReachStatusControllerTest {
  private static final String STATUS_URL = "http://localhost:%s/innreach/v2/status";
  private static final String ACTUATOR_HEALTH_URL = "http://localhost:%s/actuator/health";
  private static HttpHeaders headers;
  private static RestTemplate restTemplate;

  @LocalServerPort
  private int port;

  @BeforeAll
  static void globalSetup() {
    headers = new HttpHeaders();
    restTemplate = new RestTemplate();
  }

  @Test
  void shouldReturnOkStatus() {
    headers.clear();
    ResponseEntity<String> response = restTemplate
      .exchange(String.format(STATUS_URL, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(),is("OK"));
  }

  @Test
  void shouldReturnBadRequestForActuatorHealthWithoutTenantHeader() throws Exception {
    headers.clear();
    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> restTemplate
      .exchange(String.format(ACTUATOR_HEALTH_URL, port), HttpMethod.GET, new HttpEntity<>(headers), String.class));
    assertThat(exception.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
  }

  @Test
  void shouldReturnActuatorHealth() {
    headers.clear();
    headers.add(XOkapiHeaders.TENANT, "test_tenant");
    ResponseEntity<String> response = restTemplate
      .exchange(String.format(ACTUATOR_HEALTH_URL, port), HttpMethod.GET, new HttpEntity<>(headers), String.class);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }
}
