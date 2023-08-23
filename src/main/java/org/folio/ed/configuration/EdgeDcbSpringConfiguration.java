package org.folio.ed.configuration;

import java.util.concurrent.Executors;

import org.folio.spring.liquibase.FolioSpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EdgeDcbSpringConfiguration {

  @Bean
  public FolioSpringLiquibase folioSpringLiquibase() {
    return new FolioSpringLiquibase();
  }

  @Bean
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(new SingleConnectionDataSource());
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ExecutorChannel retrievalQueueRecordFlow() {
    return new ExecutorChannel(Executors.newCachedThreadPool());
  }
}
