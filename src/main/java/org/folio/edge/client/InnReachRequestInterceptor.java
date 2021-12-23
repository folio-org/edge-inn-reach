package org.folio.edge.client;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import static org.folio.edge.external.InnReachHttpHeaders.X_D2IR_AUTHORIZATION;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class InnReachRequestInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate template) {
    var authorizationValue = template.headers().get(AUTHORIZATION);
    template.removeHeader(AUTHORIZATION);
    template.header(X_D2IR_AUTHORIZATION, authorizationValue);
  }

}
