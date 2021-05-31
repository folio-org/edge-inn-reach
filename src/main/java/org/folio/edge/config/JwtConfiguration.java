package org.folio.edge.config;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class JwtConfiguration {

  private static final SignatureAlgorithm DEFAULT_SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

  @Value("${folio.jwt.signature.algorithm}")
  private String jwtSignatureAlgorithm;

  @Value("${folio.jwt.signature.secret}")
  private String jwtSignatureSecret;

  @Value("${folio.jwt.expiration-time-ms}")
  private long expirationTimeMs;

  @Value("${folio.jwt.claims.issuer}")
  private String issuer;

  private SignatureAlgorithm signatureAlgorithm;
  private SecretKey secretKey;

  @PostConstruct
  public void initConfig() {
    this.signatureAlgorithm = defineSignatureAlgorithm();
    this.secretKey = new SecretKeySpec(jwtSignatureSecret.getBytes(), signatureAlgorithm.getJcaName());
  }

  private SignatureAlgorithm defineSignatureAlgorithm() {
    if (isJwtSignatureAlgorithmInitialized()) {
      return SignatureAlgorithm.forName(jwtSignatureAlgorithm);
    }
    return DEFAULT_SIGNATURE_ALGORITHM;
  }

  private boolean isJwtSignatureAlgorithmInitialized() {
    return StringUtils.isNotBlank(jwtSignatureAlgorithm);
  }
}
