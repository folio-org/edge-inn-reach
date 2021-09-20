package org.folio.edge.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.folio.edge.config.props.SystemUserProperties;
import org.folio.edge.domain.FolioExecutionContextBuilder;
import org.folio.edge.domain.service.impl.SystemUserService;
import org.folio.spring.FolioExecutionContext;
import org.folio.spring.scope.FolioExecutionScopeExecutionContextManager;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class FolioExecutionContextAspect {

  private final SystemUserProperties systemUserProperties;
  private final SystemUserService systemUserService;

  @Around(value = "@annotation(org.folio.edge.aspect.annotation.WithinSystemUserExecutionContext)")
  public Object interceptWithinSystemUserExecutionContext(ProceedingJoinPoint joinPoint) throws Throwable {
    log.debug("Intercept to create folio execution context with system user...");

    var systemUser = systemUserService.getSystemUser(systemUserProperties.getOkapiTenant());

    var systemUserFolioExecutionContext = new FolioExecutionContextBuilder(null).forSystemUser(systemUser);

    return intercept(joinPoint, systemUserFolioExecutionContext);
  }

  @Around(value = "@annotation(org.folio.edge.aspect.annotation.WithinTenantExecutionContext)")
  public Object interceptWithinTenantExecutionContext(ProceedingJoinPoint joinPoint) throws Throwable {
    log.debug("Intercept to create folio execution context with tenant...");

    var folioExecutionContext = new FolioExecutionContextBuilder.Builder(null)
      .withTenantId(systemUserProperties.getOkapiTenant())
      .withOkapiUrl(systemUserProperties.getOkapiUrl())
      .withToken(systemUserProperties.getOkapiToken())
      .withUsername(systemUserProperties.getUsername())
      .build();

    return intercept(joinPoint, folioExecutionContext);
  }

  private Object intercept(ProceedingJoinPoint joinPoint, FolioExecutionContext folioExecutionContext) throws Throwable {
    try {
      FolioExecutionScopeExecutionContextManager.beginFolioExecutionContext(folioExecutionContext);
      return joinPoint.proceed(joinPoint.getArgs());
    } finally {
      FolioExecutionScopeExecutionContextManager.endFolioExecutionContext();
    }
  }
}
