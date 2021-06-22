package org.folio.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EdgeInnReachApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeInnReachApplication.class, args);
	}
}
