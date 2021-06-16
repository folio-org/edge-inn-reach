package org.folio.edge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * This is just a demo controller for testing the implementation of the JWT token mechanism,
 * will be removed when the implementation is complete.
 */

@RestController
@RequestMapping("/innreach")
public class ProtectedDemoResource {

  @GetMapping("/demo")
  public String helloMsg() {
    return "Demo!";
  }

}
