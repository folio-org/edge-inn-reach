package org.folio.edge.config;

import lombok.RequiredArgsConstructor;
import org.folio.edge.authentication.JwtAuthenticationConverter;
import org.folio.edge.authentication.filter.ExceptionHandlerFilter;
import org.folio.edge.authentication.filter.JwtTokenVerifyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtConfiguration jwtConfiguration;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
      .disable()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .anyRequest()
      .authenticated()
      .and()
      .addFilterBefore(new JwtTokenVerifyFilter(new JwtAuthenticationConverter(jwtConfiguration)), UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(new ExceptionHandlerFilter(), JwtTokenVerifyFilter.class);
  }

  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }
}
