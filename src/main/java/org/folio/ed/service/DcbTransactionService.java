package org.folio.ed.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.folio.ed.client.DcbClient;
import org.folio.ed.domain.dto.AccessionItem;
import org.folio.ed.domain.dto.AccessionRequest;
import org.folio.ed.domain.dto.CheckInItem;
import org.folio.ed.domain.dto.CheckInRequest;
import org.folio.ed.domain.dto.Configuration;
import org.folio.ed.domain.dto.DcbTransactionRequest;
import org.folio.ed.domain.dto.RetrievalQueueRecord;
import org.folio.ed.domain.dto.ReturnItemResponse;
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
