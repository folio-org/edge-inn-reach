package org.folio.edge.domain.service.impl;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import org.folio.edge.client.AuthnClient;
import org.folio.edge.client.PermissionsClient;
import org.folio.edge.config.props.SystemUserProperties;
import org.folio.edge.domain.FolioExecutionContextBuilder;
import org.folio.edge.domain.dto.systemuser.Permission;
import org.folio.edge.domain.dto.systemuser.Permissions;
import org.folio.edge.domain.dto.systemuser.SystemUser;
import org.folio.edge.domain.dto.systemuser.User;
import org.folio.edge.domain.dto.systemuser.UserCredentials;
import org.folio.edge.domain.service.UserService;
import org.folio.spring.integration.XOkapiHeaders;

@Log4j2
@Service
@RequiredArgsConstructor
public class SystemUserService {

  private final PermissionsClient permissionsClient;
  private final AuthnClient authnClient;
  private final UserService userService;
  private final SystemUserProperties folioSystemUserConf;

  public void setupSystemUser() {
    log.info("Preparing system user...");

    var folioUser = userService.getUserByName(folioSystemUserConf.getUsername());

    var userId = folioUser.map(User::getId)
      .orElse(UUID.randomUUID().toString());

    if (folioUser.isPresent()) {
      log.info("Setting up existing system user");
      addPermissions(userId);

    } else {
      log.info("No system user exist, creating...");
      createFolioUser(userId);
      saveCredentials();
      assignPermissions(userId);
    }

    log.info("System user has been prepared");
  }

  private void addPermissions(String userId) {
    var expectedPermissions = getResourceLines(folioSystemUserConf.getPermissionsFilePath());
    var assignedPermissions = permissionsClient.getUserPermissions(userId);

    if (isEmpty(expectedPermissions)) {
      throw new IllegalStateException("No permissions found to assign to user with id: " + userId);
    }

    var permissionsToAdd = new HashSet<>(expectedPermissions);
    if (assignedPermissions != null) {
      assignedPermissions.getResult().forEach(permissionsToAdd::remove);
    }

    permissionsToAdd.forEach(permission ->
      permissionsClient.addPermission(userId, Permission.of(permission)));
  }

  private void createFolioUser(String id) {
    final var user = createUserObject(id);
    userService.saveUser(user);
  }

  private User createUserObject(String id) {
    final var user = new User();

    user.setId(id);
    user.setActive(true);
    user.setUsername(folioSystemUserConf.getUsername());

    user.setPersonal(new User.Personal());
    user.getPersonal().setLastName(folioSystemUserConf.getLastname());

    return user;
  }

  private void saveCredentials() {
    authnClient.saveCredentials(UserCredentials.of(folioSystemUserConf.getUsername(), folioSystemUserConf.getPassword()));

    log.info("Saved credentials for user: [{}]", folioSystemUserConf.getUsername());
  }

  private void assignPermissions(String userId) {
    List<String> perms = getResourceLines(folioSystemUserConf.getPermissionsFilePath());

    if (isEmpty(perms)) {
      throw new IllegalStateException("No permissions found to assign to user with id: " + userId);
    }

    var permissions = Permissions.of(UUID.randomUUID().toString(), userId, perms);

    permissionsClient.assignPermissionsToUser(permissions);
  }

  @Cacheable(cacheNames = "system-user-cache", sync = true)
  public SystemUser getSystemUser(String tenantId) {
    log.info("Attempting to issue token for system user [tenantId={}]", tenantId);
    var systemUser = SystemUser.builder()
      .tenantId(folioSystemUserConf.getOkapiTenant())
      .username(folioSystemUserConf.getUsername())
      .okapiUrl(folioSystemUserConf.getOkapiUrl())
      .build();

    var token = loginSystemUser(systemUser);

    log.info("Token for system user has been issued [tenantId={}]", tenantId);
    return systemUser.withToken(token);
  }

  private String loginSystemUser(SystemUser systemUser) {
      UserCredentials creds = UserCredentials.of(systemUser.getUsername(), folioSystemUserConf.getPassword());

      var response = authnClient.getApiKey(creds);

      List<String> tokenHeaders = response.getHeaders().get(XOkapiHeaders.TOKEN);

      return Optional.ofNullable(tokenHeaders)
        .filter(list -> !CollectionUtils.isEmpty(list))
        .map(list -> list.get(0))
        .orElseThrow(() -> new IllegalStateException(String.format("User [%s] cannot log in", systemUser.getUsername())));
  }

  @SneakyThrows
  private static List<String> getResourceLines(String permissionsFilePath) {
    var resource = new ClassPathResource(permissionsFilePath);
    return IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
  }
}
