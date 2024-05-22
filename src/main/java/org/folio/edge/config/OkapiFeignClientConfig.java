package org.folio.edge.config;

import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;

import org.folio.edge.client.error.FeignErrorDecoder;

@AllArgsConstructor
@Log4j2
public class OkapiFeignClientConfig {

  @Bean
  public ErrorDecoder feignErrorDecoder() {
    return new FeignErrorDecoder();
  }

}
