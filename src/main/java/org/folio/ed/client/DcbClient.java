package org.folio.ed.client;

import org.folio.ed.client.config.OkapiFeignClientConfig;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "transactions", configuration = OkapiFeignClientConfig.class)
public interface DcbClient {

  @GetMapping("/{dcbTransactionId}/status")
  ResponseEntity<TransactionStatusResponse> getDcbTransactionStatus(@PathVariable("dcbTransactionId") String dcbTransactionId);
}
