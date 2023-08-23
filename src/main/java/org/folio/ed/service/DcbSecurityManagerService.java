package org.folio.ed.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ed.security.DcbSecureStoreFactory;
import org.folio.ed.security.SecureTenantsProducer;
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;
import org.folio.edgecommonspring.security.SecurityManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;
import static org.folio.edge.api.utils.util.PropertiesUtil.getProperties;

@Service
@RequiredArgsConstructor
@Log4j2
public class DcbSecurityManagerService {

  private final SecurityManagerService sms;

  private static final Pattern COMMA = Pattern.compile(",");
  private static final String API_KEY_TEMPLATE = "{\"t\":\"%s\", \"u\":\"%s\", \"s\":\"%s\"}";

  @Value("${secure_store}")
  private String secureStoreType;

  @Value("${secure_store_props}")
  private String secureStorePropsFile;

  @Value("${caia_soft_tenants}")
  private String caiaSoftTenants;

  @Value("${caia_soft_client}")
  private String caiaSoftClient;

  @Getter
  private Set<String> caiaSoftUserTenants = new HashSet<>();

  @PostConstruct
  public void init() {
    log.debug("init:: Started Initializing DcbSecurityManagerService");
    var secureStoreProps = getProperties(secureStorePropsFile);
    var secureStore = DcbSecureStoreFactory.getSecureStore(secureStoreType, secureStoreProps);
    var tenants = SecureTenantsProducer.getTenants(secureStoreProps, secureStore, caiaSoftTenants);
    tenants.ifPresent(tenantsStr -> caiaSoftUserTenants = Arrays.stream(COMMA.split(tenantsStr))
      .collect(toSet()));
  }

  public ConnectionSystemParameters getConnectionParameters(String tenantId) {
    log.debug("getConnectionParameters:: Trying to get connection parameters for tenantId: {}", tenantId);
    var apikey = Base64.getEncoder()
      .encodeToString(String.format(API_KEY_TEMPLATE, tenantId, caiaSoftClient, caiaSoftClient)
      .getBytes(StandardCharsets.UTF_8));
    log.info("getConnectionParameters:: Got connection parameters for tenantId: {}", tenantId);
    return sms.getParamsWithToken(apikey);
  }
}
