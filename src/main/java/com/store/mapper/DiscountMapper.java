package com.store.mapper;

import com.store.dto.discountDTOs.DiscountCreateDTO;
import com.store.dto.discountDTOs.DiscountDTO;
import com.store.dto.discountDTOs.DiscountUpdateDTO;
import com.store.entity.Discount;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface DiscountMapper {

    DiscountDTO toDto(Discount discount);

    List<DiscountDTO> toDto(List<Discount> discountList);

    List<DiscountDTO> toDto(Page<Discount> discount);

    Discount toEntity(DiscountCreateDTO discountCreateDTO);

    Discount toEntity(DiscountUpdateDTO discountUpdateDTO);

    Discount toEntity(DiscountDTO discountDTO);
}