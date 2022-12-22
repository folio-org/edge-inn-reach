package org.folio.edge.security.filter;

import static org.folio.edge.utils.CredentialsUtils.parseBasicAuth;
import static org.folio.spring.integration.XOkapiHeaders.AUTHORIZATION;
import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;
import static org.folio.edge.external.InnReachHttpHeaders.X_FROM_CODE;
import static org.folio.edge.external.InnReachHttpHeaders.X_TO_CODE;

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
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;
import org.folio.edgecommonspring.domain.entity.RequestWithHeaders;

@Log4j2
@RequiredArgsConstructor
public class EdgeSecurityFilter extends OncePerRequestFilter {

  private final List<String> securityFilterIgnoreURIList;
  private final SecurityService securityService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    if (doNotFilter(request)) {
      log.debug("JWT token verification isn't needed, since requested URI [{}] is in the ignore URIs list", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    var okapiParameters = getOkapiConnectionParameters(request.getHeader(AUTHORIZATION));

    var requestWrapper = new RequestWithHeaders(request);
    requestWrapper.putHeader(TOKEN, okapiParameters.getOkapiToken());
    requestWrapper.putHeader(TENANT, okapiParameters.getTenantId());
    /*
      * added as edge-common-spring is removing / not capturing headers apart from x-okapi-token
      * and as per d2ir specification inn-reach module require x-to-code and x-from-code
      * and for edge to work it requires authorization header
    */
    requestWrapper.putHeader(AUTHORIZATION.toLowerCase(), request.getHeader(AUTHORIZATION));
    requestWrapper.putHeader(X_TO_CODE, request.getHeader(X_TO_CODE));
    requestWrapper.putHeader(X_FROM_CODE, request.getHeader(X_FROM_CODE));

    //end

    filterChain.doFilter(requestWrapper, response);
  }

  private boolean doNotFilter(HttpServletRequest request) {
    return securityFilterIgnoreURIList.contains(request.getRequestURI());
  }

  private ConnectionSystemParameters getOkapiConnectionParameters(String authToken) {
    log.debug("getOkapiConnectionParameters :: parameter authToken : {}", authToken);
    var edgeApiKey = EdgeApiKeyHolder.getEdgeApiKey();

    if (StringUtils.isNotEmpty(edgeApiKey)) {
      log.info("Edge Api Key is not empty");
      return securityService.getOkapiConnectionParameters(edgeApiKey);
    }

    var localServerCredentials = parseBasicAuth(authToken);
    return securityService.getInnReachConnectionParameters(localServerCredentials.getKey());
  }
}
