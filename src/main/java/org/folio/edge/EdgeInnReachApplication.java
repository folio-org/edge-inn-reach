package org.folio.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {
  DataSourceAutoConfiguration.class,
  HibernateJpaAutoConfiguration.class,
  DataSourceTransactionManagerAutoConfiguration.class
})
@EnableSwagger2
public class EdgeInnReachApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeInnReachApplication.class, args);
	}

}
