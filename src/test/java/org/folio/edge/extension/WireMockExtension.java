package org.folio.edge.extension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class WireMockExtension extends WireMockServer implements BeforeAllCallback, AfterAllCallback,
    BeforeEachCallback, AfterEachCallback {

  private static final String DEFAULT_OKAPI_PROPERTY = "okapi_url";

  private String okapiProperty;


  public WireMockExtension() {
    this(DEFAULT_OKAPI_PROPERTY);
  }

  public WireMockExtension(String okapiProperty) {
    this(okapiProperty, WireMockConfiguration.options());
  }

  public WireMockExtension(String okapiProperty, Options options) {
    super(options);

    this.okapiProperty = okapiProperty;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    start();
    
    System.setProperty(okapiProperty, this.baseUrl());
  }

  @Override
  public void afterAll(ExtensionContext context) {
    stop();

    System.clearProperty(okapiProperty);
  }

  @Override
  public void beforeEach(ExtensionContext context) {
  }

  @Override
  public void afterEach(ExtensionContext context) {
    resetAll();
  }

}