package com.store.restcontroller;

import com.store.constants.ProductType;
import com.store.constants.Role;
import com.store.dto.productDTOs.ProductCreateDTO;
import com.store.dto.productDTOs.ProductDTO;
import com.store.dto.productDTOs.ProductUpdateDTO;
import com.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Get all products", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/products")
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @Operation(summary = "Get products by category", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/product")
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productService.getProductsByCategoryId(categoryId, pageable);
    }

    @Operation(summary = "Get filtered products", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/products/filter")
    public Page<ProductDTO> getFilteredProducts(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) BigDecimal min,
                                                @RequestParam(required = false) BigDecimal max,
                                                @RequestParam(required = false) String productStatus,
                                                Pageable pageable) {
        return productService.getFilteredProducts(name, categoryId, min, max, productStatus, pageable);
    }

    @Operation(summary = "Get best-sellers", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("products/best-sellers")
    public Page<ProductDTO> getProductsOrderByUnitsSold() {
        return productService.getProductsOrderByUnitsSold();
    }


    @Operation(summary = "Get products by ProductType(INDOORS/OUTD0ORS)", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/products/type")
    public Page<ProductDTO> getProductsByProductType(@RequestParam ProductType productType, Pageable pageable) {
        return productService.getProductsByProductType(productType, pageable);
    }

    @Operation(summary = "Get one product by id", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/products/{id}")
    public ProductDTO getOneProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }


    @Operation(summary = "Add product", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ProductDTO> addProduct(@Validated @RequestBody ProductCreateDTO productCreateDTO) {
        return productService.addProduct(productCreateDTO);
    }

    @Operation(summary = "Update product", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping("/products")
    public ProductDTO updateProduct(@Validated @RequestBody ProductUpdateDTO productUpdateDTO) {
        return productService.updateProduct(productUpdateDTO);
    }

    @Operation(summary = "Delete product", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}