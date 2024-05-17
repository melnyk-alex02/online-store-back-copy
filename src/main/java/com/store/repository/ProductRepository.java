package com.store.repository;

import com.store.constants.ProductType;
import com.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findProductByNameContainsIgnoreCase(String name, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAllByProductTypeAndProductStatus(ProductType productType, String productStatus, Pageable pageable);
}