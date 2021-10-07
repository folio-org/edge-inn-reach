package org.folio.edge.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class TestUtil {

  public static final String TEST_TOKEN =
      "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbm5yZWFjaENsaWVudCIsInVzZXJfaWQiOiI1ZDE3YTAzNy1hNWY2LTU0NzUtYjRmOC1jYmNkNjg0MjliMmEiLCJ0ZW5hbnQiOiJ0ZXN0X3RlbmFudCJ9.r-_5wYXKgSxDIVY_SptQef4v69BQHVL7VotGzwet8Zc";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String randomUUIDString() {
    return UUID.randomUUID().toString();
  }

  public static String randomFiveCharacterCode() {
    return randomUUIDString().substring(0, 5);
  }

  @SneakyThrows
  public static String readFileContentAsString(String path) {
    var resource = TestUtil.class.getResource(path);

    if (Objects.isNull(resource)) {
      throw new RuntimeException("Can't find file by path: " + path);
    }

    return Files.readString(Paths.get(resource.toURI())).trim();
  }

  @SneakyThrows
  public static <T> T deserializeFromJsonFile(String path, Class<T> type) {
    return objectMapper.readValue(TestUtil.class.getResource(path), type);
  }

}
