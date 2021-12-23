package org.folio.edge.security.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import static org.folio.edge.external.InnReachHttpHeaders.X_D2IR_AUTHORIZATION;
import static org.folio.edge.external.InnReachHttpHeaders.X_TO_CODE;
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

import org.folio.edge.security.service.SecurityService;
import org.folio.edge.security.store.EdgeApiKeyHolder;
import org.folio.edge.utils.RequestWithModifiableHeaders;
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;

@Log4j2
@RequiredArgsConstructor
public class EdgeSecurityFilter extends OncePerRequestFilter {

  private final List<String> securityFilterIgnoreURIList;
  private final SecurityService securityService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    if (doNotFilter(request)) {
      log.info("JWT token verification isn't needed, since requested URI [{}] is in the ignore URIs list", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    var okapiParameters = getOkapiConnectionParameters(request.getHeader(X_TO_CODE));

    var requestWrapper = new RequestWithModifiableHeaders(request);
    requestWrapper.putHeader(TOKEN, okapiParameters.getOkapiToken());
    requestWrapper.putHeader(TENANT, okapiParameters.getTenantId());
    requestWrapper.renameHeader(AUTHORIZATION, X_D2IR_AUTHORIZATION);

    filterChain.doFilter(requestWrapper, response);
  }

  private boolean doNotFilter(HttpServletRequest request) {
    return securityFilterIgnoreURIList.contains(request.getRequestURI());
  }

  private ConnectionSystemParameters getOkapiConnectionParameters(String xToCode) {
    var edgeApiKey = EdgeApiKeyHolder.getEdgeApiKey();

    if (StringUtils.isNotEmpty(edgeApiKey)) {
      return securityService.getOkapiConnectionParameters(edgeApiKey);
    }
    return securityService.getInnReachConnectionParameters(xToCode);
  }
}
