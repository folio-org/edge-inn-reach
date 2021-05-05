package org.folio.edge.component;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class StatusCheck implements HealthIndicator {

  @Override
  public Health health() {
    if (DpendenciesOk()) return Health.up().build();
    log.log(Level.ERROR, "Dependencies for EDGE-INN-REACH is failed!");
    return Health.down().build();
  }

  private boolean DpendenciesOk() {
    // TODO: 05.05.21 Code for status validation for dependencies should be placed here.
    return true;
  }
}
