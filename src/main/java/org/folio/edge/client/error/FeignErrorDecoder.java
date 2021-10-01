package org.folio.edge.client.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import org.folio.edge.domain.exception.EdgeServiceException;

@Log4j2
public class FeignErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String s, Response response) {
    if (HttpStatus.valueOf(response.status()).equals(HttpStatus.UNAUTHORIZED)) {
      log.info("CentralServer authentication failed with status code: {}", response.status());
      return new BadCredentialsException("Token authentication failed");
    }

    log.info("InnReach request failed with status [{}]", response.status());
    return new EdgeServiceException(response.status(), response.reason());
  }
}
