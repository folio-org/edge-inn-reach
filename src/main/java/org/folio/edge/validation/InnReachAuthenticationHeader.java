package org.folio.edge.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = BasicAuthenticationValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InnReachAuthenticationHeader {

  String message() default "Incorrect authentication token format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
