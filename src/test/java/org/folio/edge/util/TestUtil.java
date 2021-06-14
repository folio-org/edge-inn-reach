package org.folio.edge.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import lombok.SneakyThrows;

public class TestUtil {

  public static String randomUUIDString() {
    return UUID.randomUUID().toString();
  }

  public static String randomFiveCharacterCode() {
    return randomUUIDString().substring(0, 5);
  }

  @SneakyThrows
  public static String readFileContentAsString(String path) {
    var resource = TestUtil.class.getResource(path);
    return Files.readString(Paths.get(resource.toURI()));
  }
}
