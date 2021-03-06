package org.folio.edge.config;

import feign.Client;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import org.folio.edge.client.OkapiFeignClientProxy;
import org.folio.edge.client.error.FeignErrorDecoder;

public class OkapiFeignClientConfig {

  @Bean
  public Client OkapiFeignClient() {
    return new OkapiFeignClientProxy(null, null);
  }

  @Bean
  public ErrorDecoder feignErrorDecoder() {
    return new FeignErrorDecoder();
  }
}
