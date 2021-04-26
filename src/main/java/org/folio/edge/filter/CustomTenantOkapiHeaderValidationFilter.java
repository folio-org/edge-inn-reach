package org.folio.edge.filter;

import lombok.RequiredArgsConstructor;
import org.folio.spring.filter.TenantOkapiHeaderValidationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component("tenantOkapiHeaderValidationFilter")
@ConditionalOnProperty(
  prefix = "folio.tenant.validation",
  name = {"enabled"},
  matchIfMissing = true
)
public class CustomTenantOkapiHeaderValidationFilter extends TenantOkapiHeaderValidationFilter {

  private final List<String> tenantOkapiHeaderValidationFilterExcludeUrls;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    if (excludeFromFiltering((HttpServletRequest) servletRequest)) {
      filterChain.doFilter(servletRequest, servletResponse);
    } else {
      super.doFilter(servletRequest, servletResponse, filterChain);
    }
  }

  private boolean excludeFromFiltering(HttpServletRequest servletRequest) {
    var requestURI = servletRequest.getRequestURI();
    return tenantOkapiHeaderValidationFilterExcludeUrls.contains(requestURI);
  }

}
