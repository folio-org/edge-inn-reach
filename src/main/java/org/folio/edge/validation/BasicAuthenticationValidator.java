package org.folio.edge.validation;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;

import java.util.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BasicAuthenticationValidator implements ConstraintValidator<InnReachAuthenticationHeader, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (!value.startsWith(BASIC_AUTH_SCHEME)) {
      return false;
    }

    var decoder = Base64.getDecoder();
    var decodedKeySecret = new String(decoder.decode(value.replaceAll(BASIC_AUTH_SCHEME, "").trim()));

    if (!decodedKeySecret.contains(":")) {
      return false;
    }

    var keySecretPair = decodedKeySecret.split(":");

    return keySecretPair.length == 2;
  }

  @Override
  public void initialize(InnReachAuthenticationHeader constraintAnnotation) {}
}
