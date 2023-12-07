package org.folio.ed.client.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.folio.spring.DefaultFolioExecutionContext;
import org.folio.spring.FolioModuleMetadata;
import org.folio.spring.integration.XOkapiHeaders;
import org.folio.spring.utils.RequestUtils;
import org.springframework.context.annotation.Bean;


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

  private static <T> void addHeaderIfPresent(RequestTemplate template, String name, T value) {
    if (value != null) template.header(name, value.toString());
  }
}
