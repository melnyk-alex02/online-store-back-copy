package com.store.service;


import com.store.dto.categoryDTOs.CategoryCreateDTO;
import com.store.dto.categoryDTOs.CategoryDTO;
import com.store.dto.categoryDTOs.CategoryUpdateDTO;
import com.store.entity.Category;
import com.store.exception.DataNotFoundException;
import com.store.exception.InvalidDataException;
import com.store.mapper.CategoryMapper;
import com.store.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDTO getCategoryById(Long categoryId) {
        return categoryMapper.toDto(categoryRepository.findById(categoryId).orElseThrow(() ->
                new DataNotFoundException("There is no category with id " + categoryId)));
    }

    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    public CategoryDTO createCategory(CategoryCreateDTO categoryCreateDTO) {
        Category category = categoryMapper.toEntity(categoryCreateDTO);

        category.setCreatedAt(ZonedDateTime.now());

        try {
            return categoryMapper.toDto(categoryRepository.save(category));
        } catch (ConstraintViolationException e) {
            throw new InvalidDataException("Please, check entered fields for duplicates and required ones");
        }
    }

    public CategoryDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO) {
        if (!categoryRepository.existsById(categoryUpdateDTO.getId())) {
            throw new DataNotFoundException("There is no category with id " + categoryUpdateDTO.getId());
        }

        Category category = categoryMapper.toEntity(categoryUpdateDTO);
        category.setUpdatedAt(ZonedDateTime.now());

        try {
            return categoryMapper.toDto(categoryRepository.save(category));
        } catch (ConstraintViolationException e) {
            throw new InvalidDataException("Please, check entered fields for duplicates and required ones");
        }
    }

    public void deleteCategory(Long id) {
        long numberOfProductInCategory = categoryRepository.countItemsByCategory(id);
        if (!categoryRepository.existsById(id)) {
            throw new DataNotFoundException("There is no category with id " + id);
        }
        if (numberOfProductInCategory > 0) {
            throw new DataNotFoundException
                    ("There are " + numberOfProductInCategory + " items in category with id " + id);
        }

        categoryRepository.deleteById(id);
    }
}