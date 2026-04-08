package org.folio.edge.config;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Getter
@Configuration
@Log4j2
public class JwtConfiguration {

  public static final int DEFAULT_TOKEN_EXPIRATION_TIME_IN_SEC = 599;

  public static final MacAlgorithm DEFAULT_SIGNATURE_ALGORITHM = Jwts.SIG.HS256;

  @Value("${folio.jwt.signature.algorithm}")
  private String jwtSignatureAlgorithm;

  @Value("${folio.jwt.signature.secret}")
  private String jwtSignatureSecret;

  @Value("${folio.jwt.expiration-time-sec}")
  private int expirationTimeSec;

  @Value("${folio.jwt.claims.issuer}")
  private String issuer;

  private MacAlgorithm signatureAlgorithm;
  private SecretKey secretKey;

  @PostConstruct
  public void initConfig() {
    log.debug("Initialize JWTConfiguration");
    this.signatureAlgorithm = defineSignatureAlgorithm();
    this.secretKey = new SecretKeySpec(jwtSignatureSecret.getBytes(), resolveJcaName(signatureAlgorithm));
    log.info("Initialization of JWTConfiguration completed.");
  }

  private MacAlgorithm defineSignatureAlgorithm() {
    log.debug("Determine the SignatureAlgorithm");
    if (isJwtSignatureAlgorithmInitialized()) {
      return (MacAlgorithm) Jwts.SIG.get().forKey(jwtSignatureAlgorithm);
    }
    return DEFAULT_SIGNATURE_ALGORITHM;
  }

  private boolean isJwtSignatureAlgorithmInitialized() {
    return StringUtils.isNotBlank(jwtSignatureAlgorithm);
  }

  private static String resolveJcaName(MacAlgorithm algorithm) {
    return algorithm.key().build().getAlgorithm();
  }

  public Date calculateExpirationTime() {
    var tokenTTL = 1000 * expirationTimeSec;
    return new Date(new Date().getTime() + tokenTTL);
  }
}
