package org.folio.edge.controller;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
class TenantControllerTest {

  @LocalServerPort
  private int localServerPort;

  @Autowired
  private TestRestTemplate testRestTemplate;


  @Test
  void getTenant() {
    ResponseEntity<String> entity = this.testRestTemplate.exchange("http://localhost:" + this.localServerPort
        + "/_/tenant", HttpMethod.GET, HttpEntity.EMPTY, String.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
  }
  
}
