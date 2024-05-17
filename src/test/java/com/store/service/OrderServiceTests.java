package com.store.service;

import com.store.constants.OrderStatus;
import com.store.constants.ProductStatus;
import com.store.dto.orderDTOs.OrderDTO;
import com.store.dto.orderDTOs.OrderProductDTO;
import com.store.entity.Category;
import com.store.entity.Order;
import com.store.entity.OrderProduct;
import com.store.entity.Product;
import com.store.entity.compositeId.OrderProductId;
import com.store.exception.DataNotFoundException;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTests {
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProductRepository productRepository;


    @InjectMocks
    private OrderService orderService;

    @Test
    public void testGetOrderByUserIdAndOrderNumber() {
        Order order = createOrderList().get(0);
        OrderDTO expectedOrderDTO = createOrderDTOList().get(0);

        String userId = "userId";

        when(currentUserService.getCurrentUserUuid()).thenReturn(userId);
        when(orderRepository.existsByNumberAndEmail(expectedOrderDTO.getNumber(), userId)).thenReturn(true);
        when(orderRepository.findOrderByEmailAndNumber(userId, order.getNumber())).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expectedOrderDTO);

        OrderDTO result = orderService.getOrderByEmailAndOrderNumber("number");

        verify(orderRepository).existsByNumberAndEmail(expectedOrderDTO.getNumber(), "userId");
        verify(orderRepository).findOrderByEmailAndNumber("userId", order.getNumber());
        verify(orderMapper).toDto(order);

        assertEquals(expectedOrderDTO.getNumber(), result.getNumber());
        assertEquals(expectedOrderDTO.getOrderStatus(), result.getOrderStatus());
        assertEquals(expectedOrderDTO.getCreatedDate(), result.getCreatedDate());
        assertEquals(expectedOrderDTO.getTotalPrice(), result.getTotalPrice());
        assertEquals(expectedOrderDTO.getOrderProductDTOList().size(), result.getOrderProductDTOList().size());
    }

    @Test
    public void testGetAllOrderByUserId() {
        List<Order> orderList = createOrderList();
        List<OrderDTO> expectedOrderDTOList = createOrderDTOList();

        String email = "mail@example.com";

        Page<Order> orderPage = new PageImpl<>(orderList);

        when(currentUserService.getCurrentUserEmail()).thenReturn(email);
        when(orderRepository.existsAllByEmail(email)).thenReturn(true);
        when(orderRepository.findAllByEmail(email, Pageable.unpaged())).thenReturn(orderPage);
        when(orderMapper.toDto(any(Order.class))).thenAnswer(invocationOnMock -> {
            Order order = invocationOnMock.getArgument(0);
            String orderNumber = order.getNumber();
            return expectedOrderDTOList.stream()
                    .filter(dto -> dto.getNumber().equals(orderNumber))
                    .findFirst()
                    .orElse(null);
        });


        Page<OrderDTO> result = orderService.getAllOrdersByUserEmail(Pageable.unpaged());

        verify(orderRepository).existsAllByEmail(email);
        verify(orderRepository).findAllByEmail(email, Pageable.unpaged());
        verify(orderMapper, times(2)).toDto(any(Order.class));

        assertEquals(expectedOrderDTOList.size(), result.getContent().size());

        assertEquals(expectedOrderDTOList.get(0).getNumber(), result.getContent().get(0).getNumber());
        assertEquals(expectedOrderDTOList.get(0).getOrderStatus(), result.getContent().get(0).getOrderStatus());
        assertEquals(expectedOrderDTOList.get(0).getCreatedDate(), result.getContent().get(0).getCreatedDate());
        assertEquals(expectedOrderDTOList.get(0).getTotalPrice(), result.getContent().get(0).getTotalPrice());
        assertEquals(expectedOrderDTOList.get(0).getOrderProductDTOList().size(), result.getContent().get(0).getOrderProductDTOList().size());

        assertEquals(expectedOrderDTOList.get(1).getNumber(), result.getContent().get(1).getNumber());
        assertEquals(expectedOrderDTOList.get(1).getOrderStatus(), result.getContent().get(1).getOrderStatus());
        assertEquals(expectedOrderDTOList.get(1).getCreatedDate(), result.getContent().get(1).getCreatedDate());
        assertEquals(expectedOrderDTOList.get(1).getTotalPrice(), result.getContent().get(1).getTotalPrice());
        assertEquals(expectedOrderDTOList.get(1).getOrderProductDTOList().size(), result.getContent().get(1).getOrderProductDTOList().size());
    }

    @Test
    public void testCancelOrder() {
        Order order = createOrderList().get(0);
        OrderDTO orderDTO = createOrderDTOList().get(0);

        String userId = "userId";

        OrderDTO expectedOrderDTO = createOrderDTOList().get(0);
        expectedOrderDTO.setOrderStatus(OrderStatus.CANCELLED.getOrderStatus());

        when(currentUserService.getCurrentUserUuid()).thenReturn(userId);
        when(orderRepository.existsByNumberAndEmail(expectedOrderDTO.getNumber(), userId)).thenReturn(true);
        when(orderRepository.findOrderByEmailAndNumber(userId, order.getNumber())).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderDTO);
        when(orderRepository.save(orderMapper.toEntity(expectedOrderDTO))).thenReturn(order);

        orderService.cancelOrder("number");

        verify(orderRepository).existsByNumberAndEmail(expectedOrderDTO.getNumber(), userId);
        verify(orderRepository).findOrderByEmailAndNumber(userId, expectedOrderDTO.getNumber());
        verify(orderMapper).toDto(order);
        verify(orderRepository).save(orderMapper.toEntity(expectedOrderDTO));
    }

//    @Test
//    public void testConfirmOrder() {
//        Order order = createOrderList().get(0);
//        OrderDTO orderDTO = createOrderDTOList().get(0);
//
//        OrderDTO expectedOrderDTO = createOrderDTOList().get(0);
//        expectedOrderDTO.setOrderStatus(OrderStatus.IN_PROGRESS);
//
//        String userId = "userId";
//
//        when(currentUserService.getCurrentUserUuid()).thenReturn(userId);
//        when(orderRepository.existsByNumberAndEmail(expectedOrderDTO.getNumber(), userId)).thenReturn(true);
//        when(orderRepository.findOrderByEmailAndNumber(userId, order.getNumber())).thenReturn(order);
//        when(orderMapper.toDto(order)).thenReturn(orderDTO);
//        when(orderRepository.save(orderMapper.toEntity(expectedOrderDTO))).thenReturn(order);
//
//        orderService.confirmOrder("number");
//
//        verify(orderRepository).existsByNumberAndEmail(expectedOrderDTO.getNumber(), "userId");
//    }

    @Test
    public void testGetAllOrders() {
        List<Order> orderList = createOrderList();
        List<OrderDTO> expectedOrderDTOList = createOrderDTOList();

        Page<Order> orderPage = new PageImpl<>(orderList);

        when(orderRepository.findAll(Pageable.unpaged())).thenReturn(orderPage);
        when(orderMapper.toDto(any(Order.class))).thenAnswer(invocationOnMock -> {
            Order order = invocationOnMock.getArgument(0);
            String orderNumber = order.getNumber();
            return expectedOrderDTOList.stream()
                    .filter(dto -> dto.getNumber().equals(orderNumber))
                    .findFirst()
                    .orElse(null);
        });
        Page<OrderDTO> result = orderService.getAllOrders(Pageable.unpaged());

        verify(orderRepository).findAll(Pageable.unpaged());
        verify(orderMapper, times(2)).toDto(any(Order.class));

        assertEquals(expectedOrderDTOList.size(), result.getContent().size());

        assertEquals(expectedOrderDTOList.get(0).getNumber(), result.getContent().get(0).getNumber());
        assertEquals(expectedOrderDTOList.get(0).getOrderStatus(), result.getContent().get(0).getOrderStatus());
        assertEquals(expectedOrderDTOList.get(0).getCreatedDate(), result.getContent().get(0).getCreatedDate());
        assertEquals(expectedOrderDTOList.get(0).getTotalPrice(), result.getContent().get(0).getTotalPrice());
        assertEquals(expectedOrderDTOList.get(0).getOrderProductDTOList().size(), result.getContent().get(0).getOrderProductDTOList().size());

        assertEquals(expectedOrderDTOList.get(1).getNumber(), result.getContent().get(1).getNumber());
        assertEquals(expectedOrderDTOList.get(1).getOrderStatus(), result.getContent().get(1).getOrderStatus());
        assertEquals(expectedOrderDTOList.get(1).getCreatedDate(), result.getContent().get(1).getCreatedDate());
        assertEquals(expectedOrderDTOList.get(1).getTotalPrice(), result.getContent().get(1).getTotalPrice());
        assertEquals(expectedOrderDTOList.get(1).getOrderProductDTOList().size(), result.getContent().get(1).getOrderProductDTOList().size());
    }

    @Test
    public void testGetOrderByUserIdAndOrderNumber_WhenNoOrderFoundForUser_ShouldThrowException() {
        String userId = "userId";

        when(currentUserService.getCurrentUserUuid()).thenReturn(userId);
        when(orderRepository.existsByNumberAndEmail("number", userId)).thenThrow(
                new DataNotFoundException("There is no order with number number for current logged user")
        );

        assertThrows(DataNotFoundException.class, () -> orderService.getOrderByEmailAndOrderNumber("number"));
    }

    @Test
    public void testGetAllOrdersByUserId_WhenNoOrdersFoundForUser_ShouldThrowException() {
        String userId = "userId";

        when(currentUserService.getCurrentUserUuid()).thenReturn(userId);
        when(orderRepository.existsAllByEmail(userId)).thenThrow(
                new DataNotFoundException("There is no orders for current logged user")
        );

        assertThrows(DataNotFoundException.class, () -> orderService.getAllOrdersByUserEmail(Pageable.unpaged()));
    }

    @Test
    public void testCancelOrder_WhenNoOrderFoundForUser_ShouldThrowException() {
        String userId = "userId";

        when(currentUserService.getCurrentUserUuid()).thenReturn(userId);
        when(orderRepository.existsByNumberAndEmail("number", userId)).thenThrow(
                new DataNotFoundException("There is no order with number number for current logged user")
        );

        assertThrows(DataNotFoundException.class, () -> orderService.cancelOrder("number"));
    }

//    @Test
//    public void testConfirmOrder_WhenNoOrderFoundForUser_ShouldThrowException() {
//        String userId = "userId";
//
//        when(currentUserService.getCurrentUserUuid()).thenReturn(userId);
//        when(orderRepository.existsByNumberAndEmail("number", userId)).thenThrow(
//                new DataNotFoundException("There is no order for current logged user with number number")
//        );
//
//        assertThrows(DataNotFoundException.class, () -> orderService.confirmOrder("number"));
//    }

    private List<Order> createOrderList() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Item 1");
        product1.setDescription("Description 1");
        product1.setCategory(category);
        product1.setProductStatus(ProductStatus.IN_STOCK.getProductStatus());
        product1.setPrice(BigDecimal.valueOf(100.99));
        product1.setImagePath("Image src 1");
        product1.setUnitsSold(1L);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Item 2");
        product2.setProductStatus(ProductStatus.IN_STOCK.getProductStatus());
        product2.setDescription("Description 2");
        product2.setCategory(category);
        product1.setPrice(BigDecimal.valueOf(99.00));
        product2.setImagePath("Image src 2");
        product2.setUnitsSold(1L);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setNumber("number");
        order1.setOrderStatus(OrderStatus.NEW.getOrderStatus());
        order1.setCreatedDate(ZonedDateTime.of(LocalDateTime.of(2023, 1, 1, 0, 0),
                ZoneId.of("UTC")));
        order1.setTotalPrice(BigDecimal.valueOf(199, 99));

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setCount(1);
        orderProduct1.setProduct(product1);
        orderProduct1.setOrderProductId(new OrderProductId(1L, 1L));
        orderProduct1.setOrder(order1);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setCount(1);
        orderProduct2.setProduct(product2);
        orderProduct2.setOrderProductId(new OrderProductId(1L, 1L));
        orderProduct2.setOrder(order1);

        order1.setOrderProductList(List.of(orderProduct1, orderProduct2));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setNumber("number1");
        order2.setOrderStatus(OrderStatus.NEW.getOrderStatus());
        order2.setCreatedDate(ZonedDateTime.of(LocalDateTime.of(2023, 11, 23, 1, 0),
                ZoneId.of("UTC")));
        order2.setTotalPrice(BigDecimal.valueOf(199, 99));


        OrderProduct orderProduct3 = new OrderProduct();
        orderProduct3.setCount(1);
        orderProduct3.setProduct(product1);
        orderProduct3.setOrderProductId(new OrderProductId(2L, 1L));
        orderProduct3.setOrder(order2);

        OrderProduct orderProduct4 = new OrderProduct();
        orderProduct4.setCount(1);
        orderProduct4.setProduct(product2);
        orderProduct4.setOrderProductId(new OrderProductId(2L, 1L));
        orderProduct4.setOrder(order2);

        order2.setOrderProductList(List.of(orderProduct3, orderProduct4));

        return List.of(order1, order2);
    }

    private List<OrderDTO> createOrderDTOList() {
        OrderDTO orderDTO1 = new OrderDTO();
        orderDTO1.setNumber("number");
        orderDTO1.setOrderStatus(OrderStatus.NEW.getOrderStatus());
        orderDTO1.setCreatedDate(ZonedDateTime.of(LocalDateTime.of(2023, 1, 1, 0, 0),
                ZoneId.of("UTC")));
        orderDTO1.setTotalPrice(BigDecimal.valueOf(199, 99));


        OrderProductDTO orderProductDTO1 = new OrderProductDTO();
        orderProductDTO1.setCount(1);
        orderProductDTO1.setProductId(1L);
        orderProductDTO1.setOrderNumber(orderDTO1.getNumber());

        OrderProductDTO orderProductDTO2 = new OrderProductDTO();
        orderProductDTO2.setCount(1);
        orderProductDTO2.setProductId(2L);
        orderProductDTO2.setOrderNumber(orderDTO1.getNumber());

        orderDTO1.setOrderProductDTOList(List.of(orderProductDTO1, orderProductDTO2));

        OrderDTO orderDTO2 = new OrderDTO();
        orderDTO2.setNumber("number1");
        orderDTO2.setOrderStatus(OrderStatus.NEW.getOrderStatus());
        orderDTO2.setCreatedDate(ZonedDateTime.of(LocalDateTime.of(2023, 11, 23, 1, 0),
                ZoneId.of("UTC")));
        orderDTO2.setTotalPrice(BigDecimal.valueOf(199, 99));


        OrderProductDTO orderProductDTO3 = new OrderProductDTO();
        orderProductDTO3.setCount(1);
        orderProductDTO3.setProductId(1L);
        orderProductDTO3.setProductPrice(BigDecimal.valueOf(100.99));
        orderProductDTO3.setProductName("Item 1");
        orderProductDTO3.setOrderNumber("number1");

        OrderProductDTO orderProductDTO4 = new OrderProductDTO();
        orderProductDTO4.setCount(1);
        orderProductDTO4.setProductId(2L);
        orderProductDTO4.setProductName("Item 2");
        orderProductDTO4.setProductPrice(BigDecimal.valueOf(99.00));
        orderProductDTO4.setOrderNumber("number1");

        orderDTO2.setOrderProductDTOList(List.of(orderProductDTO3, orderProductDTO4));

        return List.of(orderDTO1, orderDTO2);
    }
}