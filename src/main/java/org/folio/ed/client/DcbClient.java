package org.folio.ed.client;

import static org.folio.edge.api.utils.Constants.APPLICATION_JSON;
import static org.folio.spring.integration.XOkapiHeaders.TENANT;
import static org.folio.spring.integration.XOkapiHeaders.TOKEN;

import org.folio.ed.domain.dto.AccessionItem;
import org.folio.ed.domain.dto.AccessionRequest;
import org.folio.ed.domain.dto.CheckInItem;
import org.folio.ed.domain.dto.CheckInRequest;
import org.folio.ed.domain.dto.Configuration;
import org.folio.ed.domain.dto.ResultList;
import org.folio.ed.domain.dto.RetrievalQueueRecord;
import org.folio.ed.domain.dto.ReturnItemResponse;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "transactions")
public interface DcbClient {

  @GetMapping("/{dcbTransactionId}/status")
  ResponseEntity<TransactionStatusResponse> getDcbTransactionStatus(
    @PathVariable("dcbTransactionId") String dcbTransactionId,
    @RequestHeader(TENANT) String tenantId,
    @RequestHeader(TOKEN) String okapiToken);

}
