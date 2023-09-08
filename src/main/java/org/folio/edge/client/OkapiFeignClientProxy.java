package org.folio.edge.client;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
@Log4j2
public class OkapiFeignClientProxy extends Client.Default {

  @Value("${okapi_url}")
  private String okapiUrl;

  public OkapiFeignClientProxy(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
    super(sslContextFactory, hostnameVerifier);
  }

  @Override
  public Response execute(Request request, Request.Options options) throws IOException {
   log.debug(" OkapiFeignClientProxy execute :: parameter request : {}, options : {}", request.toString(), options.toString());
    if (!okapiUrl.endsWith("/")) {
      okapiUrl = okapiUrl + "/";
    }

    String url = request.url().replaceAll("http://|https://", okapiUrl);

    Request proxiedRequest = Request.create(request.httpMethod(), url, request.headers(), request.body(), request
      .charset(), request.requestTemplate());

    log.info("Execute proxy request {}", proxiedRequest.toString());
    return super.execute(proxiedRequest, options);
  }

}
