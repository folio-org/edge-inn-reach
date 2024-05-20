package org.folio.edge.controller.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseControllerTest {

  protected static WireMockServer wireMock =
    new WireMockServer(new WireMockConfiguration().dynamicPort().notifier(new Slf4jNotifier(true)));

  @DynamicPropertySource
  static void registerOkapiURL(DynamicPropertyRegistry registry) {
    registry.add("folio.client.okapiUrl", () -> wireMock.baseUrl());
    registry.add("folio.client.tls.enabled", () -> false);
    log.info("OKAPI Url: {}", wireMock.baseUrl());
  }

  @BeforeAll
  static void beforeAll() {
    wireMock.start();
    log.info("Wire mock started");
  }

  @AfterAll
  static void afterAll() {
    wireMock.stop();
    log.info("Wire mock stopped");
  }

  @AfterEach
  void tearDown() {
    wireMock.resetAll();
  }

}
