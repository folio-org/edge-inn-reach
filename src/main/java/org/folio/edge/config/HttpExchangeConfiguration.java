package org.folio.edge.config;

import org.folio.edge.client.InnReachAuthClient;
import org.folio.edge.client.InnReachClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HttpExchangeConfiguration {

  @Qualifier("edgeHttpServiceProxyFactory")
  private final HttpServiceProxyFactory factory;

  /**
   * Creates a {@link InnReachAuthClient} bean.
   *
   * @return the {@link InnReachAuthClient} instance
   */
  @Bean
  public InnReachAuthClient innReachAuthClient() {
    return factory.createClient(InnReachAuthClient.class);
  }

  /**
   * Creates a {@link InnReachClient} bean.
   *
   * @return the {@link InnReachClient} instance
   */
  @Bean
  public InnReachClient innReachClient() {
    return factory.createClient(InnReachClient.class);
  }
}
