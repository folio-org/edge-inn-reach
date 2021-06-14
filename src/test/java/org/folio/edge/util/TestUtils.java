package org.folio.edge.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  public static String readStringFromFile(String path) {
    var file = new File(TestUtils.class.getResource(path).toURI());
    return Files.readString(Paths.get(file.toURI()));
  }

  @SneakyThrows
  public static <T> T deserializeFromJsonFile(String path, Class<T> type) {
    return objectMapper.readValue(TestUtils.class.getResource(path), type);
  }
}
