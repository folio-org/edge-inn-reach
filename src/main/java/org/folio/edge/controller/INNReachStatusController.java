package org.folio.edge.controller;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Log4j2
@RestController("innReachStratusController")
public class INNReachStatusController {
  private final OkHttpClient client = new OkHttpClient().newBuilder()
    .build();

  @GetMapping("/innreach/v2/status")
  public ResponseEntity<String> okResponse(Model model, HttpServletRequest request) throws IOException {
    var newRequest = new Request.Builder()
      .url("http://" + request.getHeader("host") + "/actuator/health")
      .method("GET", null)
      .addHeader("X-Okapi-Tenant", "testtenant")
      .build();
      var response = client.newCall(newRequest).execute();
      return response.code() == HttpStatus.OK.value() ?
        ResponseEntity.ok("OK") : ResponseEntity.status(response.code()).body("");
  }
}
