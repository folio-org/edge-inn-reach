package org.folio.ed.controller;

import org.folio.ed.domain.dto.CreateDCBTransactionRequest;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.rest.resource.TransactionsApi;
import org.folio.ed.service.DcbTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/dcbService/")
public class DcbTransactionController implements TransactionsApi {
  private final DcbTransactionService dcbTransactionService;

  @Override
  public ResponseEntity<TransactionStatusResponse> getDCBTransactionStatus(String dcbTransactionId) {
      return dcbTransactionService.getDcbTransactionStatus(dcbTransactionId);
  }

  @Override
  public ResponseEntity<TransactionStatus> createDCBTransaction(String dcbTransactionId, CreateDCBTransactionRequest createDCBTransactionRequest) {
    //Need to be developed
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new TransactionStatus());
  }

  @Override
  public ResponseEntity<TransactionStatusResponse> updateDCBTransactionStatus(String dcbTransactionId, TransactionStatus transactionStatus) {
    //Need to be developed
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new TransactionStatusResponse());
  }
}
