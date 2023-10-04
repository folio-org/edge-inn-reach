package org.folio.ed.controller;

import org.folio.ed.domain.dto.DcbTransaction;
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
  public ResponseEntity<TransactionStatusResponse> getDCBTransactionStatusById(String dcbTransactionId) {
    return ResponseEntity.status(HttpStatus.OK)
      .body(dcbTransactionService.getDcbTransactionStatusById(dcbTransactionId));
  }

  @Override
  public ResponseEntity<TransactionStatusResponse> createDCBTransaction(String dcbTransactionId, DcbTransaction dcbTransaction) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(dcbTransactionService.createDCBTransaction(dcbTransactionId, dcbTransaction));
  }

  @Override
  public ResponseEntity<TransactionStatusResponse> updateDCBTransactionStatus(String dcbTransactionId, TransactionStatus transactionStatus) {
    return ResponseEntity.status(HttpStatus.OK)
      .body(dcbTransactionService.updateDCBTransactionStatus(dcbTransactionId, transactionStatus));
  }
}
