package org.folio.edge.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.folio.edge.client.InnReachAuthClient;
import org.folio.edge.client.InnReachClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpExchangeConfiguration {

  private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
      "transfer-encoding", "connection", "keep-alive", "proxy-authenticate",
      "proxy-authorization", "te", "trailers", "upgrade");

  @Bean
  public InnReachAuthClient innReachAuthClient(
      @Qualifier("edgeHttpServiceProxyFactory") HttpServiceProxyFactory factory) {
    return factory.createClient(InnReachAuthClient.class);
  }

  /**
   * Creates {@link InnReachClient} with a plain RestClient (no Okapi URL enrichment),
   * because requests use a fully-qualified URI built by {@link org.folio.edge.domain.InnReachRequestBuilder}.
   * Content compression is disabled so Apache HttpClient 5 does not auto-add Accept-Encoding.
   * Hop-by-hop response headers (e.g. Transfer-Encoding) are stripped before building the
   * ResponseEntity so they are not forwarded to the calling client by the proxy controller.
   */
  @Bean
  public InnReachClient innReachClient() {
    var httpClient = HttpClients.custom().disableContentCompression().build();
    var restClient = RestClient.builder()
        .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
        .requestInterceptor((request, body, execution) -> filterHopByHop(execution.execute(request, body)))
        .build();
    var adapter = RestClientAdapter.create(restClient);
    var factory = HttpServiceProxyFactory.builderFor(adapter).build();
    return factory.createClient(InnReachClient.class);
  }

  private static ClientHttpResponse filterHopByHop(ClientHttpResponse original) {
    var filtered = new HttpHeaders();
    original.getHeaders().forEach((name, values) -> {
      if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase(Locale.ROOT))) {
        filtered.addAll(name, values);
      }
    });
    return new ClientHttpResponse() {
      @Override public HttpStatusCode getStatusCode() throws IOException { return original.getStatusCode(); }
      @Override public String getStatusText() throws IOException { return original.getStatusText(); }
      @Override public HttpHeaders getHeaders() { return filtered; }
      @Override public InputStream getBody() throws IOException { return original.getBody(); }
      @Override public void close() { original.close(); }
    };
  }
}
