package org.folio.edge.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BasicAuthenticationValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InnReachAuthenticationHeader {

  String message() default "Incorrect authentication token format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
