package org.folio.edge.utils;

import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.folio.edge.api.utils.model.ClientInfo;

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

  public static ClientInfo parseApiKey(String apiKey) throws MalformedApiKeyException {
    ClientInfo ret;
    try {
      String decoded = new String(Base64.getUrlDecoder().decode(apiKey.getBytes()));
      ret = new ObjectMapper().readValue(decoded, ClientInfo.class);
    } catch (Exception e) {
      throw new MalformedApiKeyException("Failed to parse", e);
    }

    if (ret.salt == null || ret.salt.isEmpty()) {
      throw new MalformedApiKeyException("Null/Empty Salt");
    }

    if (ret.tenantId == null || ret.tenantId.isEmpty()) {
      throw new MalformedApiKeyException("Null/Empty Tenant");
    }

    if (ret.username == null || ret.username.isEmpty()) {
      throw new MalformedApiKeyException("Null/Empty Username");
    }

    return ret;
  }

  public static class MalformedApiKeyException extends Exception {

    private static final long serialVersionUID = 7852873967223950947L;
    public static final String MSG = "Malformed API Key";

    public MalformedApiKeyException(String msg, Throwable t) {
      super(MSG + ": " + msg, t);
    }

    public MalformedApiKeyException(String msg) {
      super(MSG + ": " + msg);
    }
  }
}
