package com.store.restcontroller;

import com.store.constants.OrderStatus;
import com.store.constants.Role;
import com.store.dto.cartProductDTOs.CartProductCreateDTO;
import com.store.dto.orderDTOs.OrderDTO;
import com.store.dto.orderDTOs.OrderUpdateDTO;
import com.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get order by order number", description = "Needs authorization header with ROLE.USER")
    @PreAuthorize("hasRole('" + Role.USER + "')")
    @GetMapping("order/{orderNumber}")
    public OrderDTO getOrderByUserId(@PathVariable String orderNumber) {

        return orderService.getOrderByEmailAndOrderNumber(orderNumber);
    }

    @Operation(summary = "Get orders by orderStatus", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("orders/status")
    public Page<OrderDTO> getAllOrdersByOrderStatus(OrderStatus orderStatus, Pageable pageable) {
        return orderService.getAllOrdersByOrderStatus(orderStatus, pageable);
    }

    @Operation(summary = "Return order by OrderNumber", description = "Needs authorization header with ROLE.USER")
    @PreAuthorize("hasRole('" + Role.USER + "')")
    @PutMapping("order/{orderNumber}/return")
    public void returnOrder(@PathVariable String orderNumber) {
        orderService.returnOrder(orderNumber);
    }

    @Operation(summary = "Export orders", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("orders/export")
    public void exportAllOrders(HttpServletResponse response) throws IOException {
        orderService.exportAllOrdersToCsv(response);
    }

    @Operation(summary = "Get all orders by email", description = "Needs authorization header with ROLE.USER")
    @PreAuthorize("hasRole('" + Role.USER + "')")
    @GetMapping("orders")
    public Page<OrderDTO> getAllOrdersByEmail(Pageable pageable) {
        return orderService.getAllOrdersByUserEmail(pageable);
    }

    @Operation(summary = "Ger order by number and email for unauthorized users", description = "does not need auth header")
    @GetMapping("orders/{email}/{orderNumber}")
    public OrderDTO getOrderByNumberAndEmailForUnauthorizedUser(@PathVariable String email, @PathVariable String orderNumber) {
        return orderService.getOrderByNumberAndEmailForUnauthorized(orderNumber, email);
    }

    @Operation(summary = "Get all orders", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("orders/all")
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @Operation(summary = "Delete order by orderNumber(setting status to cancelled)", description = "Needs authorization header with ROLE.ADMIN")
    @DeleteMapping("order/{orderNumber}")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable String orderNumber) {
        orderService.cancelOrder(orderNumber);
    }

    @Operation(summary = "Create order", description = "Dont needs authorization header, but will work if you have one")
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody List<CartProductCreateDTO> cartProductCreateDTOList,
                                String shippingAddress,
                                String billingAddress,
                                Long shippingMethod,
                                String paymentMethod,
                                String commentOfManager,
                                String email,
                                boolean agreementToTerms,
                                boolean emailMeWithOffersAndNews,
                                Optional<String> discountCode) {
        return orderService.createOrder(cartProductCreateDTOList,
                shippingAddress,
                billingAddress,
                shippingMethod,
                paymentMethod,
                email,
                commentOfManager,
                agreementToTerms,
                emailMeWithOffersAndNews,
                discountCode);
    }

    @Operation(summary = "Update order details(shipping type, billing address and shipping address)", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping("order")
    public OrderDTO updateOrder(@RequestBody OrderUpdateDTO orderUpdateDTO) {
        return orderService.updateOrder(orderUpdateDTO);
    }


    @Operation(summary = "Updating status of order by orderNumber", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping("order/{orderNumber}")
    public void updateOrderStatus(@PathVariable String orderNumber, OrderStatus orderStatus) {
        orderService.updateStatusOfOrder(orderStatus, orderNumber);
    }
}