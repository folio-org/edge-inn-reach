package org.folio.ed.client.config;

import org.folio.spring.integration.XOkapiHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Exception handler for Feign client errors, passing them back to the edge API caller.
 *
 * Overall flow here, since it's not very obvious: Errors from Feign clients configured with
 * {@link OkapiFeignClientConfig} are turned into instances of {@link OkapiFeignClientErrorWrapperException}. This class
 * then handles those, convering them into {@link ResponseEntity} objects that get returned to the original caller.
 */
@ControllerAdvice
public class OkapiFeignClientExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(OkapiFeignClientErrorWrapperException.class)
  public ResponseEntity<Object> handleFeignError(OkapiFeignClientErrorWrapperException e) {
    var headers = new HttpHeaders();
    for (Map.Entry<String, Collection<String>> header : e.getHeaders().entrySet()) {
      // Copy all the headers except any Okapi headers, to prevent internal FOLIO details from leaking
      if (header.getKey().startsWith(XOkapiHeaders.OKAPI_HEADERS_PREFIX)) {
        headers.addAll(header.getKey(), new ArrayList<>(header.getValue()));
      }
    }

    return ResponseEntity
      .status(e.getCode())
      .headers(headers)
      .body(e.getBody());
  }
}
