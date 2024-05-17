package com.store.service;

import com.store.constants.OrderStatus;
import com.store.constants.ProductStatus;
import com.store.dto.cartProductDTOs.CartProductCreateDTO;
import com.store.dto.discountDTOs.DiscountDTO;
import com.store.dto.orderDTOs.OrderDTO;
import com.store.dto.orderDTOs.OrderProductDTO;
import com.store.dto.orderDTOs.OrderUpdateDTO;
import com.store.entity.Order;
import com.store.entity.OrderProduct;
import com.store.entity.Product;
import com.store.entity.ProductSize;
import com.store.entity.compositeId.OrderProductId;
import com.store.exception.DataNotFoundException;
import com.store.exception.InvalidDataException;
import com.store.mapper.OrderMapper;
import com.store.repository.DiscountRepository;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import com.store.repository.ShippingMethodRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.store.utils.OrderNumberAndDiscountCodeGenerator.generateOrderNumber;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final DiscountService discountService;
    private final CurrentUserService currentUserService;
    private final ShippingMethodRepository shippingMethodRepository;
    private final DiscountRepository discountRepository;

    static double TAX = 0.1; // as 10%

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    public Page<OrderDTO> getAllOrdersByOrderStatus(OrderStatus orderStatus, Pageable pageable) {
        logger.info("Order status {}", orderStatus.getOrderStatus());
        return orderRepository.findAllByOrderStatus(orderStatus.getOrderStatus(), pageable).map(orderMapper::toDto);
    }

    public OrderDTO getOrderByEmailAndOrderNumber(String orderNumber) {
        String email = currentUserService.getCurrentUserEmail();

        if (!orderRepository.existsByNumberAndEmail(orderNumber, email)) {
            throw new DataNotFoundException("There is no order with number " + orderNumber + " for current logged user");
        }

        logger.info(orderRepository.findOrderByNumberAndEmail(orderNumber, email).getStatusLastUpdatedAt().toString());

        return orderMapper.toDto(orderRepository.findOrderByNumberAndEmail(orderNumber, email));
    }

    public Page<OrderDTO> getAllOrdersByUserEmail(Pageable pageable) {
        String email = currentUserService.getCurrentUserEmail();

        return orderRepository.findAllByEmail(email, pageable).map(orderMapper::toDto);
    }

    public OrderDTO createOrder(List<CartProductCreateDTO> cartProductCreateDTOList,
                                String shippingAddress,
                                String billingAddress,
                                Long shippingMethodId,
                                String paymentMethod,
                                String email,
                                String commentOfManager,
                                boolean agreementToTerms,
                                boolean emailMeWithOffersAndNews,
                                Optional<String> discountCode) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal priceOfProducts = BigDecimal.ZERO;
        Order order = new Order();
        order.setNumber(generateOrderNumber());
        order.setCreatedDate(ZonedDateTime.now());
        order.setOrderStatus(OrderStatus.NEW.getOrderStatus());
        order.setEmail(email);

        order.setShippingMethod(shippingMethodRepository.getReferenceById(shippingMethodId));
        order.setAgreementToTerms(agreementToTerms);
        order.setEmailMeWithOffersAndNews(emailMeWithOffersAndNews);
        order.setCommentOfManager(commentOfManager);

        List<OrderProduct> orderProductList = new ArrayList<>();
        for (CartProductCreateDTO cartProductCreateDTO : cartProductCreateDTOList) {
            Product product = productRepository.getReferenceById(cartProductCreateDTO.getProductId());
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderProductId(new OrderProductId(order.getId(), product.getId()));
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setProductSize(cartProductCreateDTO.getSize());

            if (cartProductCreateDTO.getCount() < 1) {
                throw new InvalidDataException("Please check count of items in your cart");
            }
            orderProduct.setCount(cartProductCreateDTO.getCount());

            orderProductList.add(orderProduct);

            BigDecimal priceOfProduct;
            if (product.isOnSale()) {
                priceOfProduct = product.getSalePrice().multiply(BigDecimal.valueOf(cartProductCreateDTO.getCount()));
            } else {
                logger.info("Product totalPrice {}", product.getPrice());
                priceOfProduct = product.getPrice().multiply(BigDecimal.valueOf(cartProductCreateDTO.getCount()));
            }
            priceOfProducts = priceOfProducts.add(priceOfProduct);
            totalPrice = totalPrice.add(priceOfProduct);
            processProduct(orderProduct.getProduct(), cartProductCreateDTO.getCount(), cartProductCreateDTO.getSize());
        }


        order.setTaxAmount(BigDecimal.valueOf(totalPrice.doubleValue() * TAX).setScale(2, RoundingMode.HALF_UP));
        logger.info("tax amount {}", order.getTaxAmount());
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setTotalPrice(totalPrice.add(order.getTaxAmount()));
        order.setPriceOfProducts(priceOfProducts);
        order.setOrderProductList(orderProductList);

        discountCode.ifPresent(discount -> applyDiscount(order, discount));
        order.setDiscount(discountRepository.findDiscountByCode(discountCode.orElse(null)).orElse(null));

        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDTO updateOrder(OrderUpdateDTO orderUpdateDTO) {
        Order order = orderRepository.findByNumber(orderUpdateDTO.getOrderNumber())
                .orElseThrow(() -> new DataNotFoundException(
                        "There is no order with number " + orderUpdateDTO.getOrderNumber())
                );

        order.setBillingAddress(orderUpdateDTO.getBillingAddress());
        order.setShippingAddress(orderUpdateDTO.getShippingAddress());
//        order.setShippingMethod(orderUpdateDTO.getShippingMethod());
        order.setPaymentMethod(orderUpdateDTO.getPaymentMethod());
        order.setCommentOfManager(orderUpdateDTO.getCommentOfManager());

        return orderMapper.toDto(orderRepository.save(order));
    }

    public void cancelOrder(String orderNumber) {
        Order order = orderRepository.findByNumber(orderNumber)
                .orElseThrow(() -> new DataNotFoundException("There is no order with number " + orderNumber));

        order.setOrderStatus(OrderStatus.CANCELLED.getOrderStatus());

        for (OrderProduct orderProduct : order.getOrderProductList()) {
            Product product = orderProduct.getProduct();

            if (product.getProductStatus().equals(ProductStatus.OUT_OF_STOCK.getProductStatus())) {
                product.setProductStatus(ProductStatus.IN_STOCK.getProductStatus());
            }
            product.setTotalQuantity(product.getTotalQuantity() + orderProduct.getCount());
            product.setUnitsSold(product.getUnitsSold() - orderProduct.getCount());
            productRepository.save(product);
        }
        orderRepository.save(order);
    }

    public void updateStatusOfOrder(OrderStatus orderStatus, String number) {
        Order order = orderRepository.findByNumber(number).orElseThrow(()
                -> new DataNotFoundException("There is no order with number " + number));

        order.setOrderStatus(orderStatus.getOrderStatus());
        order.setStatusLastUpdatedAt(ZonedDateTime.now());
        orderRepository.save(order);
    }

    public void returnOrder(String orderNumber) {
        String email = currentUserService.getCurrentUserEmail();

        if (!orderRepository.existsByNumberAndEmail(orderNumber, email)) {
            throw new DataNotFoundException("There is no order for current logged user with number " + orderNumber);
        }
        Order order = orderRepository.findOrderByEmailAndNumber(email, orderNumber);

        for (OrderProduct orderProduct : order.getOrderProductList()) {
            Product product = orderProduct.getProduct();

            if (product.getProductStatus().equals(ProductStatus.OUT_OF_STOCK.getProductStatus())) {
                product.setProductStatus(ProductStatus.IN_STOCK.getProductStatus());
            }
            product.setUnitsSold(product.getUnitsSold() - orderProduct.getCount());
            product.setTotalQuantity(product.getTotalQuantity() + orderProduct.getCount());

            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.RETURN_PROCESSING.getOrderStatus());
        orderRepository.save(order);
    }

    public void exportAllOrdersToCsv(HttpServletResponse response) throws IOException {
        List<OrderDTO> orderDTOList = orderMapper.toDto(orderRepository.findAll());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=orders.csv");

        String[] headers = {"Number", "Email", "Status", "Created Date", "Product Name", "Product Count", "Total Price", "Total Count",
                "Shipping Address", "Shipping Method", "Payment Method"};

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.POSTGRESQL_CSV.builder().setHeader(headers).build())) {
            for (OrderDTO orderDTO : orderDTOList) {
                for (OrderProductDTO orderProductDTO : orderDTO.getOrderProductDTOList()) {
                    csvPrinter.printRecord(
                            orderDTO.getNumber(),
                            orderDTO.getEmail(),
                            orderDTO.getOrderStatus(),
                            orderDTO.getCreatedDate(),
                            orderProductDTO.getProductName(),
                            orderProductDTO.getCount(),
                            orderDTO.getTotalPrice(),
                            orderDTO.getShippingAddress(),
                            orderDTO.getShippingMethodDTO().getDescription(),
                            orderDTO.getPaymentMethod());
                }
            }
        }
    }

    private void applyDiscount(Order order, String code) {
        DiscountDTO discountDTO = discountService.getDiscountByCode(code);

        if (discountDTO.getExpirationDate().isBefore(ZonedDateTime.now())) {
            throw new InvalidDataException("Discount with code " + code + " is expired");
        }
        if (discountDTO.getMinPrice().doubleValue() > order.getTotalPrice().doubleValue()) {
            throw new InvalidDataException("Discount with code " + code + " can't be applied to order with number "
                    + order.getNumber() + " because order price is less than min price for this discount");
        }

        order.setDiscountAmount(BigDecimal.valueOf(order.getTotalPrice().doubleValue() * discountDTO.getDiscount().doubleValue() / 100).setScale(2, RoundingMode.HALF_UP));
        order.setDiscount(discountRepository.getReferenceById(discountDTO.getId()));
        order.setTotalPrice(BigDecimal.valueOf(order.getTotalPrice().doubleValue() - (order.getTotalPrice().doubleValue() * discountDTO.getDiscount().doubleValue() / 100)).setScale(2, RoundingMode.HALF_UP));
    }

    private void processProduct(Product product, int count, String size) {
        ProductSize productSize = product.getProductSizes().stream().filter(ps-> ps.getSize().name().equals(size))
                .findFirst().orElseThrow(() -> new InvalidDataException("There is no such size for product " + product.getName()));

        if (productSize.getQuantity() - count == 0) {
            productSize.setProductStatus(ProductStatus.OUT_OF_STOCK);
            productSize.setQuantity(0);
            product.setTotalQuantity(product.getTotalQuantity() - count);
            product.setUnitsSold(product.getUnitsSold() + count);
        } else if (productSize.getQuantity() - count < 0) {
            throw new InvalidDataException("There is no enough products in stock." +
                    " Please, lower the count of product(" + product.getName() +
                    " with size " + productSize.getSize() + ") in your cart");
        } else {
            product.setUnitsSold(product.getUnitsSold() + count);
            productSize.setQuantity(productSize.getQuantity() - count);
            product.setTotalQuantity(product.getTotalQuantity() - count);
        }

        List<ProductSize> productSizes = product.getProductSizes();
        productSizes.set(productSizes.indexOf(productSize), productSize);
        product.setProductSizes(productSizes);
        if(product.getTotalQuantity() == 0) {
            product.setProductStatus(ProductStatus.OUT_OF_STOCK.getProductStatus());
        }

        productRepository.save(product);
    }

    public OrderDTO getOrderByNumberAndEmailForUnauthorized(String orderNumber, String email) {
        if (!orderRepository.existsByNumberAndEmail(orderNumber, email)) {
            throw new DataNotFoundException("There is no order with number " + orderNumber);
        }

        return orderMapper.toDto(orderRepository.findOrderByNumberAndEmail(orderNumber, email));
    }
}