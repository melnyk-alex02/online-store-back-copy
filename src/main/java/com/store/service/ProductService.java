package com.store.service;

import com.store.constants.ProductStatus;
import com.store.constants.ProductType;
import com.store.dto.productDTOs.ProductCreateDTO;
import com.store.dto.productDTOs.ProductDTO;
import com.store.dto.productDTOs.ProductUpdateDTO;
import com.store.entity.Product;
import com.store.entity.ProductColorSizes;
import com.store.entity.ProductSize;
import com.store.exception.DataNotFoundException;
import com.store.exception.InvalidDataException;
import com.store.filterSpecefication.ProductSpecification;
import com.store.mapper.ProductMapper;
import com.store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);


    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    public Page<ProductDTO> getFilteredProducts(String name,
                                                Long categoryId,
                                                BigDecimal min,
                                                BigDecimal max,
                                                String productStatus,
                                                Pageable pageable) {
        Specification<Product> productSpecification = ProductSpecification.hasProductName(name)
                .and(ProductSpecification.hasProductStatus(productStatus))
                .and(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.hasPriceBetween(min, max));

        return productRepository.findAll(productSpecification, pageable).map(productMapper::toDto);
    }

    public Page<ProductDTO> getProductsByProductType(ProductType productType, Pageable pageable) {
        return productRepository.findAllByProductTypeAndProductStatus(productType, ProductStatus.IN_STOCK.getProductStatus(), pageable).map(productMapper::toDto);
    }

    public Page<ProductDTO> getProductsOrderByUnitsSold() {
        return productRepository.findAll(PageRequest.of(0, 5, Sort.Direction.DESC, "unitsSold")).map(productMapper::toDto);
    }

    public ProductDTO getProductById(Long id) {
        return productMapper.toDto(productRepository.findById(id).
                orElseThrow(() -> new DataNotFoundException("There is no product with id" + id)));
    }

    public Page<ProductDTO> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findAllByCategoryId(categoryId, pageable).map(productMapper::toDto);
    }

    public List<ProductDTO> addProduct(ProductCreateDTO productCreateDTO) {
        if (!productRepository.existsByCategoryId(productCreateDTO.getCategoryId())) {
            throw new DataNotFoundException("There is no category with id " + productCreateDTO.getCategoryId());
        }

        List<ProductDTO> createdProducts = new ArrayList<>();

        int num = 0;
        for (ProductColorSizes productColorSizes : productCreateDTO.getProductColorSizes()) {
            Product product = productMapper.toEntity(productCreateDTO);
            product.setCreatedAt(ZonedDateTime.now());
            product.setTotalQuantity(productColorSizes.getProductSizes()
                    .stream()
                    .map(ProductSize::getQuantity)
                    .reduce(0, Integer::sum));
            product.setArticle(productCreateDTO.getArticle() + num++);
            product.setColor(productColorSizes.getColor());
            product.setProductSizes(productColorSizes.getProductSizes());
            product.setUnitsSold(0L);
            product.setName(productCreateDTO.getName() + " " + productColorSizes.getColor());

            createdProducts.add(productMapper.toDto(productRepository.save(product)));
        }
        return createdProducts;
    }

    public ProductDTO updateProduct(ProductUpdateDTO productUpdateDTO) {
        if (!productRepository.existsById(productUpdateDTO.getId())) {
            throw new DataNotFoundException("There is no product with id: " + productUpdateDTO.getId());
        }

        Product product = productMapper.toEntity(productUpdateDTO);
        if (productUpdateDTO.getTotalQuantity() == 0) {
            product.setProductStatus(ProductStatus.OUT_OF_STOCK.getProductStatus());
        }
        product.setUpdatedAt(ZonedDateTime.now());

        try {
            return productMapper.toDto(productRepository.save(product));
        } catch (ConstraintViolationException e) {
            throw new InvalidDataException("Please, check entered fields for duplicates and required ones");
        }
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new DataNotFoundException("There is no product with id: " + id);
        }
        productRepository.deleteById(id);
    }
}