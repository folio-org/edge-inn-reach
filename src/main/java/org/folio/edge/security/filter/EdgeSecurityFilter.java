package org.folio.edge.security.filter;

import static org.folio.edge.external.InnReachHttpHeaders.X_FROM_CODE;
import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import org.folio.edge.domain.dto.ConnectionSystemParameters;
import org.folio.edge.domain.dto.HttpRequestHeadersWrapper;
import org.folio.edge.security.service.SecurityManagerService;
import org.folio.edge.security.store.EdgeApiKeyHolder;

@Log4j2
@RequiredArgsConstructor
public class EdgeSecurityFilter extends OncePerRequestFilter {

  private final List<String> securityFilterIgnoreURIList;
  private final SecurityManagerService securityManagerService;

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      FilterChain filterChain) throws ServletException, IOException {

    if (doNotFilter(httpServletRequest)) {
      log.info("JWT token verification isn't needed, since requested URI [{}] is in the ignore URIs list", httpServletRequest.getRequestURI());
      filterChain.doFilter(httpServletRequest, httpServletResponse);
      return;
    }

    var okapiConnectionParameters = getOkapiConnectionParameters(httpServletRequest.getHeader(X_FROM_CODE));

    var requestWrapper = new HttpRequestHeadersWrapper(httpServletRequest);
    requestWrapper.putHeader(TOKEN, okapiConnectionParameters.getOkapiToken());
    requestWrapper.putHeader(TENANT, okapiConnectionParameters.getTenantId());

    filterChain.doFilter(requestWrapper, httpServletResponse);
  }

  private boolean doNotFilter(HttpServletRequest request) {
    return securityFilterIgnoreURIList.contains(request.getRequestURI());
  }

  private ConnectionSystemParameters getOkapiConnectionParameters(String xFromCode) {
    var edgeApiKey = EdgeApiKeyHolder.getEdgeApiKey();

    if (StringUtils.isNotEmpty(edgeApiKey)) {
      return securityManagerService.getOkapiConnectionParameters(edgeApiKey);
    }
    return securityManagerService.getInnReachConnectionParameters(xFromCode);
  }
}
