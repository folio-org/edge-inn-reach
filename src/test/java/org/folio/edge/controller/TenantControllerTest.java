package org.folio.edge.controller;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
@Disabled
class TenantControllerTest {

  @LocalServerPort
  private int localServerPort;

  @Value("${local.management.port}")
  private int localManagementPort;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void postTenant() {
  }

  @Test
  void getTenant() {
    @SuppressWarnings("rawtypes")
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Okapi-Tenant", "testtenant");
    ResponseEntity<String> entity = this.testRestTemplate.exchange("http://localhost:" + this.localServerPort + "/_/tenant", HttpMethod.GET, new HttpEntity<>(headers), String.class);
    then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
  }

  @Test
  void getTenantWithoutHeaderShouldReturnBadRequest() throws Exception {
    @SuppressWarnings("rawtypes")
    ResponseEntity<String> entity = this.testRestTemplate.getForEntity(
      "http://localhost:" + this.localServerPort + "/_/tenant", String.class);
    then(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
