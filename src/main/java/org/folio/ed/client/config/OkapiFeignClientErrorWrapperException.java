package org.folio.ed.client.config;

import java.util.Collection;
import java.util.Map;

/**
 * Wrapper for Feign client exceptions, to expose data from error responses for {@link OkapiFeignClientExceptionHandler} to use.
 */
public class OkapiFeignClientErrorWrapperException extends RuntimeException {
  private final int code;
  private final String body;
  private final Map<String, Collection<String>> headers;

  public OkapiFeignClientErrorWrapperException(int code, String body, String reason, Map<String, Collection<String>> headers) {
    super(reason);
    this.code = code;
    this.body = body;
    this.headers = headers;
  }

  public int getCode() {
    return code;
  }

  public String getBody() {
    return body;
  }

  public Map<String, Collection<String>> getHeaders() {
    return headers;
  }
}
