package org.folio.ed.service;

import org.folio.ed.client.DcbClient;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class DcbTransactionService {

  private final DcbClient dcbClient;

  public ResponseEntity<TransactionStatusResponse> getDcbTransactionStatus(
          String dcbTransactionId,
          String xOkapiTenant,
          String xOkapiToken) {
    log.info("getDcbTransactionStatus:: Getting transaction status for id: {}, xOkapiTenant {}" +
      ", xOkapiToken {}", dcbTransactionId, xOkapiTenant, xOkapiToken);
    return dcbClient.getDcbTransactionStatus(dcbTransactionId, xOkapiTenant, xOkapiToken);
  }

}
