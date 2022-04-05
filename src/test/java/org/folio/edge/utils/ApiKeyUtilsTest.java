package org.folio.edge.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.folio.edge.api.utils.model.ClientInfo;
import org.folio.edge.api.utils.util.ApiKeyParser;

@ExtendWith({
    RandomBeansExtension.class
})
class ApiKeyUtilsTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void generateApiKey_when_saltTenantUsernameProvided(@Random String salt, @Random String tenantId,
      @Random String username) throws IOException {

    var key = CredentialsUtils.generateApiKey(salt, tenantId, username);

    var decodedKey = Base64.getUrlDecoder().decode(key);
    var actualClientInfo = objectMapper.readValue(decodedKey, ClientInfo.class);

    assertEquals(salt, actualClientInfo.salt);
    assertEquals(tenantId, actualClientInfo.tenantId);
    assertEquals(username, actualClientInfo.username);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void raiseException_when_saltIsEmpty(String salt) {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> CredentialsUtils.generateApiKey(salt, "tenant", "user")
    );

    Assertions.assertThat(exception).hasMessage("ClientID/Salt cannot be null");
  }

  @ParameterizedTest
  @NullAndEmptySource
  void raiseException_when_tenantIsEmpty(String tenantId) {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> CredentialsUtils.generateApiKey("salt", tenantId, "user")
    );

    Assertions.assertThat(exception).hasMessage("TenantID cannot be null");
  }

  @ParameterizedTest
  @NullAndEmptySource
  void raiseException_when_userNameIsEmpty(String username) {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> CredentialsUtils.generateApiKey("salt", "tenant", username)
    );

    Assertions.assertThat(exception).hasMessage("Username cannot be null");
  }

  @Test
  void returnParsedResult_from_ApiKeyParser(@Random String apiKey, @Random ClientInfo clientInfo)
      throws ApiKeyParser.MalformedApiKeyException {

    try (MockedStatic<ApiKeyParser> parser = Mockito.mockStatic(ApiKeyParser.class)) {
      parser.when(() -> ApiKeyParser.parseApiKey(apiKey)).thenReturn(clientInfo);

      var actual = CredentialsUtils.parseApiKey(apiKey);

      assertEquals(clientInfo, actual);
    }
  }

}
