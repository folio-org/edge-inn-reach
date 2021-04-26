package org.folio.edge.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController("innReachStratusController")
public class INNReachStatusController {
  OkHttpClient client = new OkHttpClient().newBuilder()
    .build();
  @GetMapping("/innreach/v2/status")
  public ResponseEntity<String> okResponse(Model model, HttpServletRequest request) {
    Request newrequest = new Request.Builder()
      .url("http://"+request.getHeader("host")+"/actuator/health")
      .method("GET", null)
      .build();
    try {
      Response response = client.newCall(newrequest).execute();
      return response.code() == HttpStatus.OK.value() ?
        ResponseEntity.ok("OK") : ResponseEntity.status(response.code()).body("");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
