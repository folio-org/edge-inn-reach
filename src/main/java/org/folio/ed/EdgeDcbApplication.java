package org.folio.ed;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static org.folio.common.utils.tls.FipsChecker.getFipsChecksResultString;

@SpringBootApplication
@EnableFeignClients
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
  HibernateJpaAutoConfiguration.class})
@Log4j2
public class EdgeDcbApplication {
  public static void main(String[] args) {
    log.info(getFipsChecksResultString());
    SpringApplication.run(EdgeDcbApplication.class, args);
  }
}
