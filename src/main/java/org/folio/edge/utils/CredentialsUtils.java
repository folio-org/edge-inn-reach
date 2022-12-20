package org.folio.edge.utils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;

import java.util.Base64;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;

import org.folio.edge.api.utils.model.ClientInfo;
import org.folio.edge.api.utils.util.ApiKeyParser;
import org.folio.edge.domain.dto.modinnreach.LocalServerCredentials;

@Log4j2
public class CredentialsUtils {

  private static final String AUTHENTICATION_TOKEN_KEY_SECRET_DELIMITER = ":";
  private static final int KEY_POSITION_IN_TOKEN = 0;
  private static final int SECRET_POSITION_IN_TOKEN = 1;

  @SneakyThrows
  public static String generateApiKey(String salt, String tenantId, String username) {
    if (isEmpty(salt)) {
      log.warn("ClientID/Salt cannot be null");
      throw new IllegalArgumentException("ClientID/Salt cannot be null");
    }

    if (isEmpty(tenantId)) {
      log.warn("TenantID cannot be null");
      throw new IllegalArgumentException("TenantID cannot be null");
    }

    if (isEmpty(username)) {
      log.warn("Username cannot be null");
      throw new IllegalArgumentException("Username cannot be null");
    }

    ClientInfo ci = new ClientInfo(salt, tenantId, username);

    return Base64.getUrlEncoder().encodeToString(new ObjectMapper().writeValueAsString(ci).getBytes());
  }

  public static ClientInfo parseApiKey(String apiKey) throws ApiKeyParser.MalformedApiKeyException {
    return ApiKeyParser.parseApiKey(apiKey);
  }

  public static LocalServerCredentials parseBasicAuth(String basicAuth) {
    try {
      var decodedAuthorizationHeader = decodeAuthorizationHeader(basicAuth);
      var keySecretArray = decodedAuthorizationHeader.split(AUTHENTICATION_TOKEN_KEY_SECRET_DELIMITER);

      log.info("Authorization header parsed successfully.");
      return LocalServerCredentials.builder()
        .key(UUID.fromString(keySecretArray[KEY_POSITION_IN_TOKEN]))
        .secret(UUID.fromString(keySecretArray[SECRET_POSITION_IN_TOKEN]))
        .build();
    } catch (Exception e) {
      log.warn("Unable to parse authorization token", e);
      throw new BadCredentialsException("Unable to parse authorization token", e);
    }
  }

  private static String decodeAuthorizationHeader(String authorization) {
    log.debug("decodeAuthorizationHeader");
    var decodedAuthorizationHeader = Base64.getDecoder()
      .decode(authorization.replaceAll(BASIC_AUTH_SCHEME, "").trim());
    log.info("Authorization header decoded");
    return new String(decodedAuthorizationHeader);
  }

}
