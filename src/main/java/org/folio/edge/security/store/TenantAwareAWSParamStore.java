package org.folio.edge.security.store;

import java.util.Optional;
import java.util.Properties;

import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import org.folio.edge.api.utils.security.AwsParamStore;

@Log4j2
public class TenantAwareAWSParamStore extends AwsParamStore {

  public static final String DEFAULT_AWS_TENANTS_KEY_PARAMETER = "innreach_tenants";
  public static final String DEFAULT_AWS_TENANTS_MAPPINGS_KEY_PARAMETER = "innreach_tenants_mappings";

  public TenantAwareAWSParamStore(Properties properties) {
    super(properties);
  }

  public Optional<String> getTenants(String innreachTenants) {
    var getParameterRequest = buildGetParameterRequest(innreachTenants, DEFAULT_AWS_TENANTS_KEY_PARAMETER);
    try {
      return getParameterFromSSM(getParameterRequest);
    } catch (Exception e) {
      log.warn("Cannot get tenants list from key: " + getParameterRequest.getName(), e);
      return Optional.empty();
    }
  }

  public Optional<String> getTenantsMappings(String innreachTenantsMappings) {
    var getParameterRequest = buildGetParameterRequest(innreachTenantsMappings, DEFAULT_AWS_TENANTS_MAPPINGS_KEY_PARAMETER);
    try {
      return getParameterFromSSM(getParameterRequest);
    } catch (Exception e) {
      log.warn("Cannot get tenants mappings list from key: " + getParameterRequest.getName(), e);
      return Optional.empty();
    }
  }

  private GetParameterRequest buildGetParameterRequest(String key, String defaultKey) {
    String name = StringUtils.isNotEmpty(key) ? key : defaultKey;
    return new GetParameterRequest().withName(name).withWithDecryption(true);
  }

  private Optional<String> getParameterFromSSM(GetParameterRequest getParameterRequest) {
    return Optional.of(this.ssm.getParameter(getParameterRequest).getParameter().getValue());
  }

}
