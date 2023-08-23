package org.folio.ed.client;

import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "transactions")
public interface DcbClient {

  @GetMapping("/{dcbTransactionId}/status")
  ResponseEntity<TransactionStatusResponse> getDcbTransactionStatus(
    @PathVariable("dcbTransactionId") String dcbTransactionId,
    @RequestHeader(TENANT) String tenantId,
    @RequestHeader(TOKEN) String okapiToken);

}
