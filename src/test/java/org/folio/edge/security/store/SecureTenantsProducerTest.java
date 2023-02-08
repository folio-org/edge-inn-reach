package org.folio.edge.security.store;

import org.folio.edge.api.utils.security.AwsParamStore;
import org.folio.edge.api.utils.security.EphemeralStore;
import org.folio.edge.api.utils.security.SecureStore;
import org.folio.edge.api.utils.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

  @Test
  void testGetTenantsMappings_withTenantAwareAWSParamStore() {
    Properties secureStoreProps = new Properties();
    TenantAwareAWSParamStore secureStore = mock(TenantAwareAWSParamStore.class);
    String innreachTenantsMappings = "mapping1,mapping2,mapping3";
    Optional<String> expected = Optional.of(innreachTenantsMappings);

    when(secureStore.getTenantsMappings(innreachTenantsMappings)).thenReturn(expected);

    Optional<String> result = SecureTenantsProducer.getTenantsMappings(secureStoreProps, secureStore, innreachTenantsMappings);

    assertEquals(expected, result);
    verify(secureStore).getTenantsMappings(innreachTenantsMappings);
  }

}
