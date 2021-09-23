package org.folio.edge.security.store;

public class EdgeApiKeyHolder {

  private static final ThreadLocal<String> edgeApiKeyHolder = new ThreadLocal<>();

  public static void setEdgeApiKey(String edgeApiKey) {
    edgeApiKeyHolder.set(edgeApiKey);
  }

  public static String getEdgeApiKey() {
    return edgeApiKeyHolder.get();
  }

  public static void clear() {
    edgeApiKeyHolder.remove();
  }
}
