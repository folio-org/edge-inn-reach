package org.folio.edge.authentication.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import org.folio.edge.dto.ErrorDto;


@Slf4j
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
      writer.write(getErrorJsonString(HttpStatus.UNAUTHORIZED.value(), e));

    } catch (Exception e) {
      log.debug("Request failed!", e);
      httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
      httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

      var writer = httpServletResponse.getWriter();
      writer.write(getErrorJsonString(HttpStatus.INTERNAL_SERVER_ERROR.value(), e));
    }
  }

  public String getErrorJsonString(int httpCode, Exception exception) throws JsonProcessingException {
    var errorDto = new ErrorDto(httpCode, exception.getMessage());
    return objectMapper.writeValueAsString(errorDto);
  }
}
