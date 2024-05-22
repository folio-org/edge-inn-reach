package org.folio.edge;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.security.Security;

import static org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider.PROVIDER_NAME;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class EdgeInnReachApplication {

	public static void main(String[] args) {
    if (Security.getProvider(PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleFipsProvider());
    }
		SpringApplication.run(EdgeInnReachApplication.class, args);
	}
}
