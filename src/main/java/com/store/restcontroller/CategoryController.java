package com.store.restcontroller;

import com.store.constants.Role;
import com.store.dto.categoryDTOs.CategoryCreateDTO;
import com.store.dto.categoryDTOs.CategoryDTO;
import com.store.dto.categoryDTOs.CategoryUpdateDTO;
import com.store.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get category by id", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable(name = "id") Long categoryId) {
        return ResponseEntity
                .ok()
                .body(categoryService.getCategoryById(categoryId));
    }

    @Operation(summary = "Get  all categories", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/category")
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return categoryService.getAllCategories(pageable);
    }

    @Operation(summary = "Add category", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PostMapping("/category")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@Validated @RequestBody CategoryCreateDTO categoryCreateDTO) {
        return categoryService.createCategory(categoryCreateDTO);
    }

    @Operation(summary = "Update category", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping("/category")
    public CategoryDTO createCategory(@Validated @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        return categoryService.updateCategory(categoryUpdateDTO);
    }

    @Operation(summary = "Update category", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @DeleteMapping("/category/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}