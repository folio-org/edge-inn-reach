package org.folio.edge.config;

import feign.Client;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import org.folio.edge.client.InnReachClientProxy;
import org.folio.edge.client.error.FeignErrorDecoder;

public class InnReachClientConfig {

  @Bean
  public Client OkapiFeignClient() {
    return new InnReachClientProxy(null, null);
  }

  @Bean
  public ErrorDecoder feignErrorDecoder() {
    return new FeignErrorDecoder();
  }
}
