package org.folio.edge.security.store;

import org.folio.edge.api.utils.security.AwsParamStore;
import org.folio.edge.api.utils.security.EphemeralStore;
import org.folio.edge.api.utils.security.SecureStore;
import org.folio.edge.api.utils.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecureTenantsProducerTest {

  @Test
  void testEmptyMappings() {

    var properties = PropertiesUtil.getProperties(null);
    properties.put(AwsParamStore.PROP_REGION, "us-east-1");

    SecureStore secureStore = SecureStoreFactory.getSecureStore(AwsParamStore.TYPE, properties);
    SecureStore ephermalSecureStore = SecureStoreFactory.getSecureStore(EphemeralStore.TYPE, PropertiesUtil.getProperties(null));
    Optional<String> tenatMapping1 = null;

    Optional<String> tenatMapping = SecureTenantsProducer.getTenants(PropertiesUtil.getProperties(null), secureStore,
      "6b583dfe-8c34-40bb-a520-5b49b23edb3d:diku");

    try {
      tenatMapping1 = SecureTenantsProducer.getTenants(PropertiesUtil.getProperties(null), ephermalSecureStore,
        "6b583dfe-8c34-40bb-a520-5b49b23edb3d:diku");
    } catch (Exception e) {
      assertNull(tenatMapping1);
    }

    assertTrue(tenatMapping.isEmpty());
  }
}
