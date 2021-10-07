package org.folio.edge.utils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.folio.edge.api.utils.model.ClientInfo;
import org.folio.edge.api.utils.util.ApiKeyParser;

public class ApiKeyUtils {

  @SneakyThrows
  public static String generateApiKey(String salt, String tenantId, String username) {
    if (isEmpty(salt)) {
      throw new IllegalArgumentException("ClientID/Salt cannot be null");
    }

    if (isEmpty(tenantId)) {
      throw new IllegalArgumentException("TenantID cannot be null");
    }

    if (isEmpty(username)) {
      throw new IllegalArgumentException("Username cannot be null");
    }

    ClientInfo ci = new ClientInfo(salt, tenantId, username);

    return Base64.getUrlEncoder().encodeToString(new ObjectMapper().writeValueAsString(ci).getBytes());
  }

  public static ClientInfo parseApiKey(String apiKey) throws ApiKeyParser.MalformedApiKeyException {
    return ApiKeyParser.parseApiKey(apiKey);
  }

}
