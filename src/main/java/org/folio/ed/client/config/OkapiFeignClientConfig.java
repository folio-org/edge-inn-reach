package org.folio.ed.client.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import org.folio.spring.DefaultFolioExecutionContext;
import org.folio.spring.FolioModuleMetadata;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.spring.utils.RequestUtils;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

/**
 * Feign client configuration to add the Okapi headers (x-okapi-tenant, x-okapi-token, x-okapi-user-id) from incoming
 * requests to outgoing Feign client requests and handle errors.
 *
 * <p />
 * Usage:
 * Use this class as the configuration in the <code>@FeignClient</code> annotation. E.g.,
 * <code>@FeignClient(name = "myClient", configuration = OkapiFeignClientConfig.class)</code>
 *
 */
public class OkapiFeignClientConfig {
  @Bean
  public RequestInterceptor requestInterceptor(FolioModuleMetadata folioModuleMetadata) {
    return template -> {
      var context = new DefaultFolioExecutionContext(folioModuleMetadata, RequestUtils.getHttpHeadersFromRequest());
      addHeaderIfPresent(template, XOkapiHeaders.TENANT, context.getTenantId());
      addHeaderIfPresent(template, XOkapiHeaders.TOKEN, context.getToken());
      addHeaderIfPresent(template, XOkapiHeaders.USER_ID, context.getUserId());
    };
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new ErrorDecoder() {
      @SneakyThrows
      @Override
      public OkapiFeignClientErrorWrapperException decode(String methodKey, Response response) {
          // Consume the body InputStream here and pass the result to the exception for OkapiFeignClientExceptionHandler
          // to use. Do it here instead of the handler because otherwise it gets closed before the handler ever sees it
          var body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
          return new OkapiFeignClientErrorWrapperException(response.status(), body, response.reason(), response.headers());
      }
    };
  }

  private static <T> void addHeaderIfPresent(RequestTemplate template, String name, T value) {
    if (value != null) template.header(name, value.toString());
  }
}
