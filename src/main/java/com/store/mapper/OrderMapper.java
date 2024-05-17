package com.store.mapper;

import com.store.dto.orderDTOs.OrderDTO;
import com.store.dto.orderDTOs.OrderProductDTO;
import com.store.entity.Order;
import com.store.entity.OrderProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Mapping(target = "shippingMethodDTO", source = "shippingMethod")
    @Mapping(source = "orderProductList", target = "orderProductDTOList")
    @Mapping(target = "discountDTO", source = "discount")
    OrderDTO toDto(Order order);


    @Mapping(target = "orderProductDTOList", source = "orderProductList")
    List<OrderDTO> toDto(List<Order> orderList);

    @Mapping(target = "shippingMethod", source = "shippingMethodDTO")
    @Mapping(source = "orderProductDTOList", target = "orderProductList")
    @Mapping(source = "discountDTO", target = "discount")
    Order toEntity(OrderDTO orderDTO);

    List<OrderProductDTO> toOrderProductDTOList(List<OrderProduct> orderProducts);

    List<OrderProduct> toOrderProductList(List<OrderProductDTO> orderProductDTOList);

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "order.number", source = "orderNumber")
    OrderProduct toOrderProduct(OrderProductDTO orderProductDTO);

    @Mapping(target = "productImagePath", source = "product.imagePath")
    @Mapping(target = "orderNumber", source = "order.number")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
    @Mapping(source = "product.onSale", target = "onSale")
    @Mapping(source = "product.salePrice", target = "salePrice")
    @Mapping(source = "product.article", target = "productArticle")
    @Mapping(source = "product.color", target = "productColor")
    OrderProductDTO toOrderProductDTO(OrderProduct orderProduct);
}