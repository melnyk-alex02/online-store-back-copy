package com.store.restcontroller;

import com.store.constants.Role;
import com.store.dto.shippingMethodDTOs.ShippingMethodCreateDTO;
import com.store.dto.shippingMethodDTOs.ShippingMethodDTO;
import com.store.dto.shippingMethodDTOs.ShippingMethodUpdateDTO;
import com.store.service.ShippingMethodService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShippingMethodController {
    private final ShippingMethodService shippingMethodService;

    @GetMapping("/shipping-methods")
    @Operation(summary = "Get all shipping methods", description = "Does not need authorization header, but will work if you have one")
    public Page<ShippingMethodDTO> getAllShippingMethods(Pageable pageable) {
        return shippingMethodService.getAllShippingMethods(pageable);
    }

    @GetMapping("/shipping-methods/{id}")
    @Operation(summary = "Get shipping method by id", description = "Does not need authorization header, but will work if you have one")
    public ShippingMethodDTO getShippingMethodById(@PathVariable Long id) {
        return shippingMethodService.getShippingMethodById(id);
    }

    @PostMapping("/shipping-methods")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @Operation(summary = "Create shipping method", description = "Needs authorization header")
    public ShippingMethodDTO createShippingMethod(@RequestBody ShippingMethodCreateDTO shippingMethodCreateDTO) {
        return shippingMethodService.createShippingMethod(shippingMethodCreateDTO);
    }

    @PutMapping("/shipping-methods")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @Operation(summary = "Update shipping method", description = "Needs authorization header")
    public ShippingMethodDTO updateShippingMethod(@RequestBody ShippingMethodUpdateDTO shippingMethodUpdateDTO) {
        return shippingMethodService.updateShippingMethod(shippingMethodUpdateDTO);
    }

    @DeleteMapping("/shipping-methods/{id}")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @Operation(summary = "Delete shipping method", description = "Needs authorization header")
    public void deleteShippingMethodById(@PathVariable Long id) {
        shippingMethodService.deleteShippingMethodById(id);
    }
}