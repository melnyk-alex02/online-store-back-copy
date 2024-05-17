package com.store.repository;

import com.store.entity.ShippingMethod;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    boolean existsById(@NotNull Long id);
}