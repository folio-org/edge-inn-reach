package org.folio.edge;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static org.folio.common.utils.tls.FipsChecker.getFipsChecksResultString;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
@Log4j2
public class EdgeInnReachApplication {

	public static void main(String[] args) {
    log.info(getFipsChecksResultString());
		SpringApplication.run(EdgeInnReachApplication.class, args);
	}
}
