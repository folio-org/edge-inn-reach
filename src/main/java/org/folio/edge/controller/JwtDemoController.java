package org.folio.edge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//todo - This is just a demo controller to verify the JWT token mechanism implementation,
// will be deleted when the implementations is finished.

@RestController
@RequestMapping("/inn-reach")
public class JwtDemoController {

  @GetMapping("/demo")
  public String helloMsg() {
    return "Hello world!";
  }

}
