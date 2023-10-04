package org.folio.ed.client;

import org.folio.ed.client.config.OkapiFeignClientConfig;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transactions", configuration = OkapiFeignClientConfig.class)
public interface DcbClient {

  @GetMapping(value = "/{dcbTransactionId}/status")
  TransactionStatusResponse getDcbTransactionStatus(@PathVariable("dcbTransactionId") String dcbTransactionId);

  @PostMapping(value = "/{dcbTransactionId}")
  TransactionStatusResponse createCirculationRequest(@PathVariable("dcbTransactionId") String dcbTransactionId,
                                                                       @RequestBody DcbTransaction dcbTransaction);
  @PutMapping(value = "/{dcbTransactionId}/status")
  TransactionStatusResponse updateTransactionStatus(@PathVariable("dcbTransactionId") String dcbTransactionId,
                                                                      @RequestBody TransactionStatus transactionStatus);
}
