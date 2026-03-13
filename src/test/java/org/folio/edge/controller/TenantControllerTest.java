package org.folio.edge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import org.folio.edge.controller.base.BaseControllerTest;

class TenantControllerTest extends BaseControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getTenant() throws Exception {
    mockMvc.perform(get("/_/tenant"))
      .andExpect(status().isUnauthorized());
  }

}
