package org.folio.edge.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import org.folio.edge.dto.Error;

@Log4j2
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      FilterChain filterChain) throws IOException {
    try {
      filterChain.doFilter(httpServletRequest, httpServletResponse);

    } catch (BadCredentialsException e) {
      log.debug("Authentication request failed!", e);

      httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);

      var writer = httpServletResponse.getWriter();
      writer.write(getErrorJsonString("invalid_token", e));

    } catch (Exception e) {
      log.error("Request failed!", e);

      httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
      httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

      var writer = httpServletResponse.getWriter();
      writer.write(getErrorJsonString(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), e));
    }
  }

  public String getErrorJsonString(String reason, Exception exception) throws JsonProcessingException {
    var error = new Error()
      .error(reason)
      .errorDescription(exception.getMessage());

    return objectMapper.writeValueAsString(error);
  }
}
