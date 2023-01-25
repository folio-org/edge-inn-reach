package org.folio.edge.controller;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import org.folio.edge.controller.base.BaseControllerTest;

@TestPropertySource(properties = {"management.port=0"})
class TenantControllerTest extends BaseControllerTest {

  @LocalServerPort
  private int localServerPort;

  @Autowired
  private TestRestTemplate testRestTemplate;


  @Test
  void getTenant() {
    ResponseEntity<String> entity = this.testRestTemplate.exchange("http://localhost:" + this.localServerPort
        + "/_/tenant", HttpMethod.GET, HttpEntity.EMPTY, String.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

}
