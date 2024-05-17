package com.store.service;

import com.store.dto.shippingMethodDTOs.ShippingMethodCreateDTO;
import com.store.dto.shippingMethodDTOs.ShippingMethodDTO;
import com.store.dto.shippingMethodDTOs.ShippingMethodUpdateDTO;
import com.store.exception.DataNotFoundException;
import com.store.mapper.ShippingMethodMapper;
import com.store.repository.ShippingMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingMethodService {
    private final ShippingMethodRepository shippingMethodRepository;
    private final ShippingMethodMapper shippingMethodMapper;

    public Page<ShippingMethodDTO> getAllShippingMethods(Pageable pageable) {
        return shippingMethodRepository.findAll(pageable).map(shippingMethodMapper::toDto);
    }

    public ShippingMethodDTO getShippingMethodById(Long id) {
        return shippingMethodMapper.toDto(shippingMethodRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("There is no shippinh method with id: " + id)));
    }

    public ShippingMethodDTO createShippingMethod(ShippingMethodCreateDTO shippingMethodCreateDTO) {
        return shippingMethodMapper.toDto(shippingMethodRepository.save(
                shippingMethodMapper.toEntity(shippingMethodCreateDTO))
        );
    }

    public ShippingMethodDTO updateShippingMethod(ShippingMethodUpdateDTO shippingMethodUpdateDTO) {
        if (!shippingMethodRepository.existsById(shippingMethodUpdateDTO.getId())) {
            throw new DataNotFoundException("There is no shipping method with id: " + shippingMethodUpdateDTO.getId());
        }

        return shippingMethodMapper.toDto(shippingMethodRepository.save(
                shippingMethodMapper.toEntity(shippingMethodUpdateDTO))
        );
    }

    public void deleteShippingMethodById(Long id) {
        if (!shippingMethodRepository.existsById(id)) {
            throw new DataNotFoundException("There is no shipping method with id: " + id);
        }

        shippingMethodRepository.deleteById(id);
    }
}