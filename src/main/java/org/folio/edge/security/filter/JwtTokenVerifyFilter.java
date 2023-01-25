package org.folio.edge.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import org.folio.edge.authentication.JwtAuthenticationConverter;

@Log4j2
@RequiredArgsConstructor
public class JwtTokenVerifyFilter extends OncePerRequestFilter {

  private final List<String> securityFilterIgnoreURIList;
  private final JwtAuthenticationConverter authenticationConverter;

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      FilterChain filterChain) throws ServletException, IOException {

    if (doNotFilter(httpServletRequest)) {
      log.debug("JWT token verification isn't needed, since requested URI [{}] is in the ignore URIs list", httpServletRequest.getRequestURI());
      filterChain.doFilter(httpServletRequest, httpServletResponse);
      return;
    }

    log.info("Start to verify JWT token, requested URI [{}]", httpServletRequest.getRequestURI());

    UsernamePasswordAuthenticationToken authenticationToken = authenticationConverter.convert(httpServletRequest);

    log.info("JWT token is verified...");

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  private boolean doNotFilter(HttpServletRequest request) {
    return securityFilterIgnoreURIList.contains(request.getRequestURI());
  }

}
