package org.folio.edge.config;

import feign.Client;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import org.folio.edge.client.InnReachRequestInterceptor;
import org.folio.edge.client.InnReachClientProxy;

public class InnReachClientConfig {

  @Bean
  public Client OkapiFeignClient() {
    return new InnReachClientProxy(null, null);
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return new InnReachRequestInterceptor();
  }
}
