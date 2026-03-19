package org.folio.edge.filter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.folio.edge.api.utils.Constants.X_OKAPI_TOKEN;
import static org.folio.edge.fixture.InnReachFixture.createInnReachHttpHeaders;
import static org.folio.edge.util.TestUtil.TEST_TOKEN;
import static org.folio.edge.util.TestUtil.readFileContentAsString;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.folio.edge.controller.base.BaseControllerTest;
import org.folio.edge.dto.AccessTokenResponse;

import tools.jackson.databind.ObjectMapper;

@Disabled
class JwtTokenVerifyFilterTest extends BaseControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setupBeforeEach() {
    wireMock.stubFor(WireMock.post(urlEqualTo("/authn/login"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.CREATED.value())
            .withHeader(X_OKAPI_TOKEN, TEST_TOKEN)));
  }

  @Test
  void return200HttpCode_when_sendRequestWithValidJwtToken() throws Exception {
    wireMock.stubFor(WireMock.post(urlEqualTo("/inn-reach/authentication")).willReturn(ok()));

    var httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + getJwtToken());

    mockMvc.perform(get("/innreach/demo").headers(httpHeaders))
      .andExpect(status().isOk());
  }

  private String getJwtToken() throws Exception {
    var httpHeaders = createInnReachHttpHeaders();

    MvcResult result = mockMvc.perform(post("/innreach/v2/oauth2/token?grant_type=client_credentials&scope=innreach_tp")
        .headers(httpHeaders))
      .andExpect(status().isOk())
      .andReturn();

    var body = objectMapper.readValue(result.getResponse().getContentAsString(), AccessTokenResponse.class);
    return body.getAccessToken();
  }

  @Test
  void return401HttpCode_when_sendRequestWithInvalidJwtToken() throws Exception {
    var invalidJwtToken = readFileContentAsString("/jwt/token/invalid-jwt-token.txt");

    var httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + invalidJwtToken);

    mockMvc.perform(get("/innreach/demo").headers(httpHeaders))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void return401HttpCode_when_sendRequestWithoutJwtToken() throws Exception {
    mockMvc.perform(get("/innreach/demo"))
      .andExpect(status().isUnauthorized());
  }
}