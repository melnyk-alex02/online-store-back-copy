package com.store.service;

import com.store.dto.discountDTOs.DiscountCreateDTO;
import com.store.dto.discountDTOs.DiscountDTO;
import com.store.dto.discountDTOs.DiscountUpdateDTO;
import com.store.exception.DataNotFoundException;
import com.store.mapper.DiscountMapper;
import com.store.repository.DiscountRepository;
import com.store.utils.OrderNumberAndDiscountCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    public Page<DiscountDTO> getAllDiscounts(Pageable pageable) {
        return discountRepository.findAll(pageable).map(discountMapper::toDto);
    }

    public DiscountDTO getDiscountByCode(String code) {
        return discountMapper.toDto(discountRepository.findDiscountByCode(code)
                .orElseThrow(() -> new DataNotFoundException("There is no discount found with code=" + code)));
    }

    public DiscountDTO createDiscount(DiscountCreateDTO discountCreateDTO) {
        DiscountDTO discountDTO = discountMapper.toDto(discountMapper.toEntity(discountCreateDTO));

        discountDTO.setCode(OrderNumberAndDiscountCodeGenerator.generateDiscountCode());
        return discountMapper.toDto(discountRepository.save(discountMapper.toEntity(discountDTO)));
    }

    public DiscountDTO updateDiscount(DiscountUpdateDTO discountUpdateDTO) {
        if (!discountRepository.existsById(discountUpdateDTO.getId())) {
            throw new DataNotFoundException("Discount with id=" + discountUpdateDTO.getId() + " was not found");
        }

        return discountMapper.toDto(discountRepository.save(discountMapper.toEntity(discountUpdateDTO)));
    }

    public void deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new DataNotFoundException("Discount with id=" + id + " was not found");
        }

        discountRepository.deleteById(id);
    }
}