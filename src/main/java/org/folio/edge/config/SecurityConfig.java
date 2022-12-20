package org.folio.edge.config;

import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.folio.edge.authentication.JwtAuthenticationConverter;
import org.folio.edge.domain.dto.JwtAccessToken;
import org.folio.edge.domain.service.AccessTokenService;
import org.folio.edge.filter.ExceptionHandlerFilter;
import org.folio.edge.security.filter.EdgeSecurityFilter;
import org.folio.edge.security.filter.JwtTokenVerifyFilter;
import org.folio.edge.security.service.SecurityService;

@RequiredArgsConstructor
@Configuration
@Log4j2
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final AccessTokenService<JwtAccessToken, Jws<Claims>> accessTokenService;
  private final SecurityService securityService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    log.debug("Configure the SecurityConfig :: parameter http : {} ", http.toString());
    http
      .csrf()
      .disable()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .antMatchers(HttpMethod.GET,"/admin/health").permitAll()
      .antMatchers(HttpMethod.POST,"/innreach/v2/oauth2/token").permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .addFilterBefore(
          new JwtTokenVerifyFilter(jwtTokenVerifyFilterIgnoreURIList(), new JwtAuthenticationConverter(accessTokenService)),
          UsernamePasswordAuthenticationFilter.class)
      .addFilterAfter(new EdgeSecurityFilter(securityFilterIgnoreURIList(), securityService), JwtTokenVerifyFilter.class)
      .addFilterBefore(new ExceptionHandlerFilter(), JwtTokenVerifyFilter.class);
    log.info("The security config is configured");
  }

  private List<String> securityFilterIgnoreURIList() {
    return List.of(
      "/admin/health"
    );
  }

  private List<String> jwtTokenVerifyFilterIgnoreURIList() {
    return List.of(
      "/admin/health",
      "/innreach/v2/oauth2/token"
    );
  }

  public static class AuthenticationScheme {
    public static final String BEARER_AUTH_SCHEME = "Bearer";
    public static final String BASIC_AUTH_SCHEME = "Basic";
  }
}
