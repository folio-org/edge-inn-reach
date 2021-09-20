package org.folio.edge.config.feign;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.folio.edge.client.error.FeignErrorDecoder;
import org.folio.edge.client.interceptor.FolioRequestInterceptor;
import org.springframework.context.annotation.Bean;

import org.folio.spring.FolioExecutionContext;

public class FolioFeignClientConfig {

  @Bean
  public RequestInterceptor requestInterceptor(FolioExecutionContext folioExecutionContext) {
    return new FolioRequestInterceptor(folioExecutionContext);
  }

  @Bean
  public ErrorDecoder feignErrorDecoder() {
    return new FeignErrorDecoder();
  }

}
