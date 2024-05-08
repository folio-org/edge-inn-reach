package org.folio.ed.service;

import org.folio.ed.client.DcbClient;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.domain.dto.TransactionStatusResponseCollection;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class DcbTransactionService {

  private final DcbClient dcbClient;

  public TransactionStatusResponse getDcbTransactionStatusById(String dcbTransactionId) {
    log.info("getDcbTransactionStatusById:: Getting transaction status for id: {}", dcbTransactionId);
    return dcbClient.getDcbTransactionStatus(dcbTransactionId);
  }

  public TransactionStatusResponse createDCBTransaction(String dcbTransactionId, DcbTransaction dcbTransaction) {
    log.info("createDCBTransaction:: Creating transaction for id: {}", dcbTransactionId);
    return dcbClient.createCirculationRequest(dcbTransactionId, dcbTransaction);
  }

  public TransactionStatusResponse updateDCBTransactionStatus(String dcbTransactionId, TransactionStatus transactionStatus) {
    log.info("updateDCBTransactionStatus:: Updating status transaction for id: {} to {}", dcbTransactionId, transactionStatus.getStatus());
    return dcbClient.updateTransactionStatus(dcbTransactionId, transactionStatus);
  }

  public TransactionStatusResponseCollection getTransactionStatusList(OffsetDateTime fromDate, OffsetDateTime toDate, Integer pageNumber,
                                                                      Integer pageSize) {
    log.info("getTransactionStatusList:: get transaction status list with fromDate {}, toDate {}, pageNumber {}, pageSize {}",
      fromDate, toDate, pageNumber, pageSize);
    return dcbClient.getTransactionStatusList(fromDate.toString(), toDate.toString(), pageNumber, pageSize);
  }

}
