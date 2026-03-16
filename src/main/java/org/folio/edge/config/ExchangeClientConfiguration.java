package org.folio.edge.config;

import java.util.function.UnaryOperator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.RestClient;

import org.folio.edge.domain.exception.EdgeServiceException;

@Configuration
public class ExchangeClientConfiguration {

  @Bean("edgeRestClientCustomizer")
  public UnaryOperator<RestClient.Builder> edgeRestClientCustomizer() {
    return restClientBuilder ->
      restClientBuilder.defaultStatusHandler(
          status -> status.value() == HttpStatus.UNAUTHORIZED.value(), (request, response) -> {
            throw new BadCredentialsException("Token authentication failed");
          })
        .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
          throw new EdgeServiceException(response.getStatusCode().value(), response.getStatusText());
        });
  }
}
