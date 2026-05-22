package org.folio.edge.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.UnaryOperator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.RestClient;

@Configuration
public class ExchangeClientConfiguration {

  @Bean("edgeRestClientCustomizer")
  public UnaryOperator<RestClient.Builder> edgeRestClientCustomizer() {
    var stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
    stringConverter.setSupportedMediaTypes(List.of(
      MediaType.TEXT_PLAIN,
      MediaType.APPLICATION_JSON,
      new MediaType("application", "*+json"),
      MediaType.ALL
    ));

    return restClientBuilder ->
      restClientBuilder.defaultStatusHandler(
          status -> status.value() == HttpStatus.UNAUTHORIZED.value(), (request, response) -> {
            throw new BadCredentialsException("Token authentication failed");
          });
  }
}
