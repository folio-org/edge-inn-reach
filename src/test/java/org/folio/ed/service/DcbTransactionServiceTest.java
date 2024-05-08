package org.folio.ed.service;

import org.folio.ed.client.DcbClient;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.folio.ed.utils.EntityUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DcbTransactionServiceTest {
  @Mock
  DcbClient dcbClient;

  @InjectMocks
  DcbTransactionService dcbTransactionService;

  @Test
  void getDcbTransactionStatusById() {
    when(dcbClient.getDcbTransactionStatus(anyString())).thenReturn(createTransactionStatusResponse(TransactionStatusResponse.StatusEnum.AWAITING_PICKUP));
    dcbTransactionService.getDcbTransactionStatusById("123");
    verify(dcbClient).getDcbTransactionStatus(anyString());
  }

  @Test
  void createDcbTransactionTest() {
    Mockito.when(dcbClient.createCirculationRequest(anyString(), any(DcbTransaction.class))).thenReturn(createTransactionStatusResponse(TransactionStatusResponse.StatusEnum.CREATED));
    dcbTransactionService.createDCBTransaction("123", createDcbTransaction());
    Mockito.verify(dcbClient).createCirculationRequest(anyString(), any(DcbTransaction.class));
  }

  @Test
  void updateDcbTransactionStatusTest() {
    Mockito.when(dcbClient.updateTransactionStatus(anyString(), any(TransactionStatus.class))).thenReturn(createTransactionStatusResponse(TransactionStatusResponse.StatusEnum.CREATED));
    dcbTransactionService.updateDCBTransactionStatus("123", createTransactionStatus(TransactionStatus.StatusEnum.OPEN));
    Mockito.verify(dcbClient).updateTransactionStatus(anyString(), any(TransactionStatus.class));
  }

  @Test
  void getTransactionStatusListTest() {
    var startDate = OffsetDateTime.now().minusDays(1);
    var endDate = OffsetDateTime.now();
    dcbTransactionService.getTransactionStatusList(startDate, endDate, 0, 100);
    Mockito.verify(dcbClient).getTransactionStatusList(startDate.toString(), endDate.toString(), 0, 100);
  }
}
