package org.folio.ed.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ed.domain.dto.CaiaSoftRequest;
import org.folio.ed.domain.dto.CaiaSoftRequestItem;
import org.folio.ed.domain.dto.Configuration;
import org.folio.ed.domain.dto.RetrievalQueueRecord;
import org.folio.ed.service.CaiaSoftSecurityManagerService;
import org.folio.ed.service.RemoteStorageService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Log4j2
public class RetrievalQueueRecordHandler {

  private static final String CAIA_SOFT_CIRCULATION_REQUEST_PATH = "/api/circrequests/v1/";
  private static final String CAIA_SOFT_API_KEY_HEADER_NAME = "X-API-Key";

  private final RestTemplate restTemplate;
  private final CaiaSoftSecurityManagerService sms;
  private final RemoteStorageService remoteStorageService;

  public Object handle(RetrievalQueueRecord record, Configuration configuration) {
    log.info("Handle retrieval record item barcode {} and request id {}", record.getItemBarcode(), record.getHoldId());
    var headers = new HttpHeaders();
    headers.add(CAIA_SOFT_API_KEY_HEADER_NAME, configuration.getApiKey());
    var caiaSoftRequest = getCaiaSoftRequest(record);
    var url = configuration.getUrl() + CAIA_SOFT_CIRCULATION_REQUEST_PATH;
    var response = post(url, headers, caiaSoftRequest);
    if (response.getStatusCode() == HttpStatus.OK) {
      var okapiToken = sms.getConnectionParameters(configuration.getTenantId()).getOkapiToken();
      remoteStorageService.setRetrieved(record.getItemBarcode(), configuration.getTenantId(), okapiToken);
      log.info("Retrieval record with item barcode {} and request id {} set retrieved", record.getItemBarcode(), record.getHoldId());
    }
    return null;
  }

  private CaiaSoftRequest getCaiaSoftRequest(RetrievalQueueRecord record) {
    var requestItem = CaiaSoftRequestItem.of(record.getItemBarcode(), record.getRequestType(), record.getHoldId());
    return CaiaSoftRequest.of(Collections.singletonList(requestItem));
  }

  private ResponseEntity<String> post(String url, HttpHeaders headers, Object entity) {
    return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(entity, headers), String.class);
  }
}
