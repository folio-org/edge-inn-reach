package org.folio.edge.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import org.folio.edge.authentication.JwtAuthenticationConverter;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenVerifyFilter extends OncePerRequestFilter {

  private final List<String> ignoreURIs;
  private final JwtAuthenticationConverter authenticationConverter;

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      FilterChain filterChain) throws ServletException, IOException {

    if (doNotFilter(httpServletRequest)) {
      filterChain.doFilter(httpServletRequest, httpServletResponse);
      return;
    }

    UsernamePasswordAuthenticationToken authRequest = authenticationConverter.convert(httpServletRequest);

    log.debug("Authentication success for user: {}", authRequest.getPrincipal());

    SecurityContextHolder.getContext().setAuthentication(authRequest);

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  private boolean doNotFilter(HttpServletRequest request) {
    return ignoreURIs.contains(request.getRequestURI());
  }

}
