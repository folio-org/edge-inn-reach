package org.folio.edge.controller;

import lombok.extern.log4j.Log4j2;
import org.folio.tenant.rest.resource.TenantApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController("folioTenantController")
@RequestMapping(value = "/_/")
public class TenantController implements TenantApi {}
