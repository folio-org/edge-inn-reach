package org.folio.edge.security.store;

import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import org.folio.edge.api.utils.security.AwsParamStore;
import org.folio.edge.api.utils.security.EphemeralStore;
import org.folio.edge.api.utils.security.SecureStore;

@Slf4j
public class SecureStoreFactory {

  private SecureStoreFactory() {
  }

  public static SecureStore getSecureStore(String type, Properties props) {
    SecureStore ret;

    if (AwsParamStore.TYPE.equals(type)) {
      ret = new TenantAwareAWSParamStore(props);
    } else {
      ret = new EphemeralStore(props);
    }

    log.info("type: {}, class: {}", type, ret.getClass().getName());
    return ret;
  }

}
