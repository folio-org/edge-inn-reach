package org.folio.edge.client;

import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import static org.folio.edge.external.InnReachHttpHeaders.X_D2IR_AUTHORIZATION;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InnReachRequestInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate template) {
    log.debug("Apply InnReachRequestInterceptor :: parameter, template: {} ", template);
    renameAuthorizationHeader(template);
    template.removeHeader(ACCEPT_ENCODING); // spring-cloud-feign prior to v3.1.0 doesn't handle content compression
    log.info("Authorization header made ready to intercept inn-reach requests.");
  }

  private void renameAuthorizationHeader(RequestTemplate template) {
    log.debug("renameAuthorizationHeader :: parameter template: {} ", template.toString());
    var authorizationValue = template.headers().get(AUTHORIZATION);
    template.removeHeader(AUTHORIZATION);
    template.header(X_D2IR_AUTHORIZATION, authorizationValue);
    log.info("renameAuthorizationHeader completed and header is in the template {} ", template.toString());
  }

}
