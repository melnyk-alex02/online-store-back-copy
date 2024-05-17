package com.store.repository;

import com.store.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"orderProductList", "orderProductList.product", "discount", "shippingMethod"})
    Order findOrderByEmailAndNumber(String email, String number);

    @EntityGraph(attributePaths = {"orderProductList", "orderProductList.product", "discount", "shippingMethod"})
    Page<Order> findAllByEmail(String email, Pageable pageable);

    @EntityGraph(attributePaths = {"orderProductList", "orderProductList.product", "discount", "shippingMethod"})
    Order findOrderByNumberAndEmail(String number, String email);

    @EntityGraph(attributePaths = {"orderProductList", "orderProductList.product", "discount", "shippingMethod"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"orderProductList", "orderProductList.product", "discount", "shippingMethod"})
    Optional<Order> findByNumber(String number);

    @EntityGraph(attributePaths = {"orderProductList", "orderProductList.product", "discount", "shippingMethod"})
    Page<Order> findAllByOrderStatus(String orderStatus, Pageable pageable);

    boolean existsAllByEmail(String userId);

    boolean existsByNumberAndEmail(String orderNumber, String email);
}