package com.store.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AttributesMapValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAttributesMap {
    String message() default "Invalid attributes map";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};

    String[] allowedKeys() default "";
}