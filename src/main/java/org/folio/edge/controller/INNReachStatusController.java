package org.folio.edge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("innReachStratusController")
public class INNReachStatusController {
  @GetMapping("/innreach/v2/status")
  public ResponseEntity okResponse() {
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }
}
