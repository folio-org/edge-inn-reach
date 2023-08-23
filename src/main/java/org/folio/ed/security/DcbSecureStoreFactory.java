package org.folio.ed.security;

import lombok.extern.log4j.Log4j2;
import org.folio.edge.api.utils.security.AwsParamStore;
import org.folio.edge.api.utils.security.SecureStore;
import org.folio.edge.api.utils.security.SecureStoreFactory;

import java.util.Properties;

@Log4j2
public class DcbSecureStoreFactory {

  private DcbSecureStoreFactory(){}

  public static SecureStore getSecureStore(String secureStoreType, Properties secureStoreProps) {
    log.debug("getSecureStore:: Retrieving SecureStore");
    if (AwsParamStore.TYPE.equals(secureStoreType)) {
      log.info("getSecureStore:: Creating new TenantAwareAWSParamStore");
      return new TenantAwareAWSParamStore(secureStoreProps);
    }
    log.info("getSecureStore:: Creating new SecureStore of type: {}", secureStoreType);
    return SecureStoreFactory.getSecureStore(secureStoreType, secureStoreProps);
  }
}
