package com.store.mapper;

import com.store.dto.shippingMethodDTOs.ShippingMethodCreateDTO;
import com.store.dto.shippingMethodDTOs.ShippingMethodDTO;
import com.store.dto.shippingMethodDTOs.ShippingMethodUpdateDTO;
import com.store.entity.ShippingMethod;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ShippingMethodMapper {
    List<ShippingMethodDTO> toDto(List<ShippingMethod> shippingMethods);
    ShippingMethodDTO toDto(ShippingMethod shippingMethod);

    ShippingMethod toEntity(ShippingMethodCreateDTO shippingMethodCreateDTO);

    ShippingMethod toEntity(ShippingMethodUpdateDTO shippingMethodUpdateDTO);
}
