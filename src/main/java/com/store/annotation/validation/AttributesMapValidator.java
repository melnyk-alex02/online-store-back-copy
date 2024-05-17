package com.store.annotation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributesMapValidator implements ConstraintValidator<ValidAttributesMap, Map<String, List<String>>> {

    private List<String> allowedKeys;

    @Override
    public void initialize(ValidAttributesMap constraintAnnotation) {
        allowedKeys = Arrays.asList(constraintAnnotation.allowedKeys());
    }

    @Override
    public boolean isValid(Map<String, List<String>> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> invalidKeys = value.keySet().stream()
                .filter(key -> !allowedKeys.contains(key))
                .collect(Collectors.toList());

        if (!invalidKeys.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("Invalid keys found in attributes map: %s", String.join(", ", invalidKeys)))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}