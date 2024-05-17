package com.store.restcontroller;

import com.store.constants.Role;
import com.store.dto.discountDTOs.DiscountCreateDTO;
import com.store.dto.discountDTOs.DiscountDTO;
import com.store.dto.discountDTOs.DiscountUpdateDTO;
import com.store.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discount")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @Operation(summary = "Get discount by code", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/{code}")
    public DiscountDTO getDiscountByCode(@PathVariable String code) {
        return discountService.getDiscountByCode(code);
    }

    @Operation(summary = "Get all discounts", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping
    public Page<DiscountDTO> getAllDiscounts(Pageable pageable) {
        return discountService.getAllDiscounts(pageable);
    }

    @Operation(summary = "Create discount", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DiscountDTO createDiscount(@RequestBody DiscountCreateDTO discountCreateDTO) {
        return discountService.createDiscount(discountCreateDTO);
    }

    @Operation(summary = "Update discount", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping
    public DiscountDTO updateDiscount(@RequestBody DiscountUpdateDTO discountUpdateDTO) {
        return discountService.updateDiscount(discountUpdateDTO);
    }

    @Operation(summary = "Delete discount", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
    }
}