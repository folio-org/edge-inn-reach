package org.folio.edge.client;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.beans.factory.annotation.Value;

public class OkapiFeignClientProxy extends Client.Default {

  @Value("${okapi.url}")
  private String okapiUrl;

  public OkapiFeignClientProxy(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
    super(sslContextFactory, hostnameVerifier);
  }

  @Override
  public Response execute(Request request, Request.Options options) throws IOException {
    if (!okapiUrl.endsWith("/")) {
      okapiUrl = okapiUrl + "/";
    }

    String url = request.url().replaceAll("http://|https://", okapiUrl);

    Request proxiedRequest = Request.create(request.httpMethod(), url, request.headers(), request.body(), request
      .charset(), request.requestTemplate());

    return super.execute(proxiedRequest, options);
  }

}
