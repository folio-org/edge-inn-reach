package org.folio.edge.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.folio.edge.domain.exception.EdgeServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String s, Response response) {
    if (HttpStatus.valueOf(response.status()).equals(HttpStatus.UNAUTHORIZED)) {
      log.debug("CentralServer authentication failed with status code: {}", response.status());
      return new BadCredentialsException("Token authentication failed");
    }

    log.debug("CentralServer authentication failed with status code: {}", response.status());
    return new EdgeServiceException("Internal server error");
  }
}
