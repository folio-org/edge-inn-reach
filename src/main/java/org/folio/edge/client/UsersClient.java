package org.folio.edge.client;

import java.util.Optional;
import java.util.UUID;

import org.folio.edge.config.feign.FolioFeignClientConfig;
import org.folio.edge.aspect.annotation.WithinTenantExecutionContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.folio.edge.domain.dto.systemuser.ResultList;
import org.folio.edge.domain.dto.systemuser.User;

@FeignClient(value = "users", configuration = FolioFeignClientConfig.class)
public interface UsersClient {

  @WithinTenantExecutionContext
  @GetMapping
  ResultList<User> getUsersByQuery(@RequestParam("query") String query);

  @WithinTenantExecutionContext
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  void saveUser(@RequestBody User user);

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  void updateUser(@PathVariable String id, @RequestBody User user);

  @GetMapping(value = "/{id}")
  Optional<User> getUserById(@PathVariable("id") UUID id);
}
