package org.folio.ed;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.security.Security;

import static org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider.PROVIDER_NAME;

@SpringBootApplication
@EnableFeignClients
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
  HibernateJpaAutoConfiguration.class})
public class EdgeDcbApplication {
  public static void main(String[] args) {
    if (Security.getProvider(PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleFipsProvider());
    }
    SpringApplication.run(EdgeDcbApplication.class, args);
  }
}
