package org.folio.edge.client;

import org.folio.edge.config.feign.FolioFeignClientConfig;
import org.folio.edge.aspect.annotation.WithinTenantExecutionContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.folio.edge.domain.dto.systemuser.Permission;
import org.folio.edge.domain.dto.systemuser.Permissions;
import org.folio.edge.domain.dto.systemuser.ResultList;

@FeignClient(value = "perms/users", configuration = FolioFeignClientConfig.class)
public interface PermissionsClient {

  @WithinTenantExecutionContext
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  Permissions assignPermissionsToUser(@RequestBody Permissions permissions);

  @WithinTenantExecutionContext
  @PostMapping(value = "/{userId}/permissions?indexField=userId", consumes = MediaType.APPLICATION_JSON_VALUE)
  void addPermission(@PathVariable("userId") String userId, Permission permission);

  @WithinTenantExecutionContext
  @GetMapping(value = "/{userId}/permissions?indexField=userId")
  ResultList<String> getUserPermissions(@PathVariable("userId") String userId);
}
