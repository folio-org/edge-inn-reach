package org.folio.edge.client;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InnReachClientProxy extends Client.Default {

  public InnReachClientProxy(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
    super(sslContextFactory, hostnameVerifier);
  }

  @Override
  public Response execute(Request request, Request.Options options) throws IOException {
    log.info("Proxying [{}] {} request", request.httpMethod().name(), request.url());
    return super.execute(request, options);
  }

}
