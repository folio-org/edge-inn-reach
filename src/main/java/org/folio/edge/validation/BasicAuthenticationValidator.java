package org.folio.edge.validation;

import lombok.extern.log4j.Log4j2;

import static org.folio.edge.config.SecurityConfig.AuthenticationScheme.BASIC_AUTH_SCHEME;

import java.util.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Log4j2
public class BasicAuthenticationValidator implements ConstraintValidator<InnReachAuthenticationHeader, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    log.debug("BasicAuthenticationValidator isValid :: parameter value : {}, context : {}",
      value, context.toString());
    if (!value.startsWith(BASIC_AUTH_SCHEME)) {
      log.info("Invalid basic auth scheme");
      return false;
    }

    var decoder = Base64.getDecoder();
    var decodedKeySecret = new String(decoder.decode(value.replaceAll(BASIC_AUTH_SCHEME, "").trim()));

    if (!decodedKeySecret.contains(":")) {
      log.info("Incorrect decoded key secret");
      return false;
    }

    var keySecretPair = decodedKeySecret.split(":");

    log.info("Basic authentication validation is valid.");
    return keySecretPair.length == 2;
  }

  @Override
  public void initialize(InnReachAuthenticationHeader constraintAnnotation) {}
}
