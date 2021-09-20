package org.folio.edge;

import org.folio.edge.config.props.SystemUserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(value = SystemUserProperties.class)
@EnableAspectJAutoProxy
public class EdgeInnReachApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeInnReachApplication.class, args);
	}
}
