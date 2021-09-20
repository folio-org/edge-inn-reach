package org.folio.edge.controller;

import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.folio.edge.domain.service.impl.SystemUserService;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.folio.tenant.rest.resource.TenantApi;

@Log4j2
@RestController("folioTenantController")
@RequestMapping(value = "/_/")
public class FolioTenantController implements TenantApi {

  private final SystemUserService systemUserService;

  public FolioTenantController(SystemUserService systemUserService) {
    this.systemUserService = systemUserService;
  }

  @Override
  public ResponseEntity<String> postTenant(@Valid TenantAttributes tenantAttributes) {
    try {
      log.info("Start initializing System user...");
      systemUserService.setupSystemUser();

    } catch (Exception e) {
      log.error("Error initializing System User", e);
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Error while initializing System User: " + e);
    }

    log.info("System user has been initializing...");
    return ResponseEntity.ok("true");
  }
}
