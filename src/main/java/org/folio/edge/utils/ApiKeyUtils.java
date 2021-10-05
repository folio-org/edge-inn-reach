package org.folio.edge.utils;

import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.folio.edge.api.utils.model.ClientInfo;
import org.folio.edge.api.utils.util.ApiKeyParser;

public class ApiKeyUtils {

  @SneakyThrows
  public static String generateApiKey(String salt, String tenantId, String username) {
    if (salt == null || salt.isEmpty()) {
      throw new IllegalArgumentException("ClientID/Salt cannot be null");
    }

    if (tenantId == null || tenantId.isEmpty()) {
      throw new IllegalArgumentException("TenantID cannot be null");
    }

    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null");
    }

    ClientInfo ci = new ClientInfo(salt, tenantId, username);

    return Base64.getUrlEncoder().encodeToString(new ObjectMapper().writeValueAsString(ci).getBytes());
  }

  public static ClientInfo parseApiKey(String apiKey) throws ApiKeyParser.MalformedApiKeyException {
    return ApiKeyParser.parseApiKey(apiKey);
  }

}
