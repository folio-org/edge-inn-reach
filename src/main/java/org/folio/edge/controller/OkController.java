package org.folio.edge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("okController")
public class OkController {
  @GetMapping("/ok")
  public String okResponse() {
    return "ok";
  }
}
