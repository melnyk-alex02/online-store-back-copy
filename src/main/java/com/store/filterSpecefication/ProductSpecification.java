package com.store.filterSpecefication;

import com.store.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> hasProductName(String name) {
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId != null) {
                return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
            }
            return null;
        };
    }

    public static Specification<Product> hasPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> {
            if (min != null && max != null) {
                return criteriaBuilder.between(root.get("price"), min, max);
            } else if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), max);
            }
            return null;
        };
    }

    public static Specification<Product> hasProductStatus(String productStatus) {
        return (root, query, criteriaBuilder) -> {
            if (!productStatus.isBlank()) {
                return criteriaBuilder.equal(root.get("productStatus"), productStatus);
            }
            return null;
        };
    }
}