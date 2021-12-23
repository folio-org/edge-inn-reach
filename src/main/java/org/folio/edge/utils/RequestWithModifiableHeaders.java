package org.folio.edge.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWithModifiableHeaders extends HttpServletRequestWrapper {
  private final Map<String, String> headers;

  public RequestWithModifiableHeaders(HttpServletRequest request) {
    super(request);
    headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    request.getHeaderNames()
      .asIterator()
      .forEachRemaining(header -> headers.put(header, request.getHeader(header)));
  }

  public void renameHeader(String name, String newName) {
    if (headers.containsKey(name)) {
      var value = headers.remove(name);
      headers.put(newName, value);
    }
  }

  public void putHeader(String name, String value) {
    headers.put(name, value);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    return Collections.enumeration(Collections.singletonList(headers.get(name)));
  }

  @Override
  public String getHeader(String name) {
    return this.headers.get(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(new HashSet<>(headers.keySet()));
  }

}
