package org.folio.edge.filter;

import static org.folio.edge.util.TestUtil.readFileContentAsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.folio.edge.domain.service.impl.AuthenticationServiceImpl;
import org.folio.edge.domain.service.impl.JwtAccessTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.folio.edge.config.JwtConfiguration;

//@WebMvcTest()
//@Import({
//  JwtConfiguration.class,
//  JwtAccessTokenServiceImpl.class,
//  AuthenticationServiceImpl.class
//})
//class JwtTokenVerifyFilterTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Test
//  void return401HttpCode_when_thereIsNoAuthorizationHeader() throws Exception {
//    mockMvc.perform(MockMvcRequestBuilders.get("/inn-reach/demo"))
//      .andExpect(status().isUnauthorized())
//      .andExpect(content().contentType("application/json"))
//      .andExpect(content().json(readFileContentAsString("/jwt/error/empty-authorization-header-error.json")));
//  }
//
//  @Test
//  void return401HttpCode_when_authorizationHeaderIsNotBearer() throws Exception {
//    mockMvc.perform(MockMvcRequestBuilders.get("/inn-reach/demo")
//      .header(HttpHeaders.AUTHORIZATION, "Basic"))
//      .andExpect(status().isUnauthorized())
//      .andExpect(content().contentType("application/json"))
//      .andExpect(content().json(readFileContentAsString("/jwt/error/invalid-authorization-scheme-error.json")));
//  }
//
//  @Test
//  void return401HttpCode_when_bearerAuthorizationHeaderIsEmpty() throws Exception {
//    mockMvc.perform(MockMvcRequestBuilders.get("/inn-reach/demo")
//      .header(HttpHeaders.AUTHORIZATION, "Bearer"))
//      .andExpect(status().isUnauthorized())
//      .andExpect(content().contentType("application/json"))
//      .andExpect(content().json(readFileContentAsString("/jwt/error/empty-bearer-authorization-token-error.json")));
//  }
//
//  @Test
//  void return401HttpCode_when_bearerJwtAuthorizationTokenIsNotValid() throws Exception {
//    mockMvc.perform(MockMvcRequestBuilders.get("/inn-reach/demo")
//      .header(HttpHeaders.AUTHORIZATION, "Bearer"))
//      .andExpect(status().isUnauthorized())
//      .andExpect(content().contentType("application/json"));
//  }
//
//  @Test
//  void return200HttpCode_when_bearerJwtAuthorizationTokenIsValid() throws Exception {
//    mockMvc.perform(MockMvcRequestBuilders.get("/inn-reach/demo")
//      .header(HttpHeaders.AUTHORIZATION, "Bearer " + readFileContentAsString("/jwt/token/jwt-simple.txt")))
//      .andExpect(status().isOk());
//  }
//
//}
