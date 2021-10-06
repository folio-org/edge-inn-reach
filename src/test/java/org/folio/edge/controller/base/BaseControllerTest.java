package org.folio.edge.controller.base;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.folio.edge.extension.WireMockExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseControllerTest {

  @RegisterExtension
  protected static WireMockExtension wireMock =
      new WireMockExtension("okapi_url", wireMockConfig().dynamicPort().notifier(new Slf4jNotifier(true)));
}
