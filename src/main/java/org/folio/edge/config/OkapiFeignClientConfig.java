package org.folio.edge.config;

import feign.Client;
import feign.codec.ErrorDecoder;
import org.folio.edge.client.FeignErrorDecoder;
import org.folio.edge.client.OkapiFeignClientProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
