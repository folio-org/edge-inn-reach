package org.folio.edge.config;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Getter
@Configuration
public class JwtConfiguration {

  public static final int DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC = 599;

  private static final SignatureAlgorithm DEFAULT_SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

  @Value("${folio.jwt.signature.algorithm}")
  private String jwtSignatureAlgorithm;

  @Value("${folio.jwt.signature.secret}")
  private String jwtSignatureSecret;

  @Value("${folio.jwt.expiration-time-sec}")
  private int expirationTimeSec;

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

  public Date calculateExpirationTime() {
    var tokenTTL = 1000 * expirationTimeSec;
    return new Date(new Date().getTime() + tokenTTL);
  }
}
