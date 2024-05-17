package com.store.repository;

import com.store.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Page<Discount> findAll(Pageable pageable);

    Optional<Discount> findDiscountByCode(String code);
}
