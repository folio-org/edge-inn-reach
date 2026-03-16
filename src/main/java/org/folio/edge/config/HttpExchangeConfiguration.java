package org.folio.edge.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.folio.edge.client.InnReachAuthClient;
import org.folio.edge.client.InnReachClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HttpExchangeConfiguration {

  @Qualifier("edgeHttpServiceProxyFactory")
  private final HttpServiceProxyFactory factory;

  @Bean
  public InnReachAuthClient innReachAuthClient() {
    return factory.createClient(InnReachAuthClient.class);
  }

  /**
   * Creates {@link InnReachClient} with a plain RestClient (no Okapi URL enrichment),
   * because requests use a fully-qualified URI built by {@link org.folio.edge.domain.InnReachRequestBuilder}.
   * Content compression is disabled so Apache HttpClient 5 does not auto-add Accept-Encoding.
   */
  @Bean
  public InnReachClient innReachClient() {
    return factory.createClient(InnReachClient.class);
  }
}
