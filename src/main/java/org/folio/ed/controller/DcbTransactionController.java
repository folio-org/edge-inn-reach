package org.folio.ed.controller;

import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.rest.resource.TransactionsApi;
import org.folio.ed.service.CaiaSoftSecurityManagerService;
import org.folio.ed.service.DcbTransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/dcbService/")
public class DcbTransactionController implements TransactionsApi {

  private final DcbTransactionService dcbTransactionService;
  private final CaiaSoftSecurityManagerService sms;

  @Override
  public ResponseEntity<TransactionStatusResponse> getDcbTransactionStatus(
    @ApiParam(required = true) @PathVariable("dcbTransactionId") String dcbTransactionId,
    @ApiParam(required = true) @RequestHeader(value = "x-okapi-token") String xOkapiToken,
    @ApiParam(required = true) @RequestHeader(value = "x-okapi-tenant") String xOkapiTenant){
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return dcbTransactionService.getDcbTransactionStatus(
      dcbTransactionId, "diku", sms.getConnectionParameters("diku").getOkapiToken());
  }

}
