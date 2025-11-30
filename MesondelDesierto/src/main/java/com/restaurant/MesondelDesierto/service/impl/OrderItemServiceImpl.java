package com.restaurant.MesondelDesierto.service.impl;

import com.restaurant.MesondelDesierto.dto.OrderItemDto;
import com.restaurant.MesondelDesierto.dto.OrderRequest;
import com.restaurant.MesondelDesierto.dto.Response;
import com.restaurant.MesondelDesierto.entity.Order;
import com.restaurant.MesondelDesierto.entity.OrderItem;
import com.restaurant.MesondelDesierto.entity.Product;
import com.restaurant.MesondelDesierto.entity.User;
import com.restaurant.MesondelDesierto.enums.OrderStatus;
import com.restaurant.MesondelDesierto.exception.NotFoundException;
import com.restaurant.MesondelDesierto.mapper.EntityDtoMapper;
import com.restaurant.MesondelDesierto.repository.OrderItemRepo;
import com.restaurant.MesondelDesierto.repository.OrderRepo;
import com.restaurant.MesondelDesierto.repository.ProductRepo;
import com.restaurant.MesondelDesierto.service.interf.OrderItemService;
import com.restaurant.MesondelDesierto.service.interf.UserService;
import com.restaurant.MesondelDesierto.specification.OrderItemSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final UserService userService;
    private final EntityDtoMapper entityDtoMapper;


    @Override
    public Response placeOrder(OrderRequest orderRequest) {

        User user = userService.getLoginUser();
        //mapear los ítems de la solicitud de pedido a las entidades de pedido.

        List<OrderItem> orderItems = orderRequest.getItems().stream().map(orderItemRequest -> {
            Product product = productRepo.findById(orderItemRequest.getProductId())
                    .orElseThrow(()-> new NotFoundException("Producto no encontrado"));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity()))); //establecer el precio de acuerdo con la cantidad.
            orderItem.setStatus(OrderStatus.PENDIENTE);
            orderItem.setUser(user);
            return orderItem;

        }).collect(Collectors.toList());

        //calcula el precio total
        BigDecimal totalPrice = orderRequest.getTotalPrice() != null && orderRequest.getTotalPrice().compareTo(BigDecimal.ZERO) > 0
                ? orderRequest.getTotalPrice()
                : orderItems.stream().map(OrderItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        //crear la entidad pedido
        Order order = new Order();
        order.setOrderItemList(orderItems);
        order.setTotalPrice(totalPrice);

        //establecer la referencia del pedido en cada ítem del pedido
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        orderRepo.save(order);

        return Response.builder()
                .status(200)
                .message("El pedido se realizo con exito")
                .build();

    }

    @Override
    public Response updateOrderItemStatus(Long orderItemId, String status) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId)
                .orElseThrow(()-> new NotFoundException("Item de pedido no encontrado"));

        orderItem.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderItemRepo.save(orderItem);
        return Response.builder()
                .status(200)
                .message("Estado del pedido actualizado correctamente")
                .build();
    }

    @Override
    public Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable) {
        Specification<OrderItem> spec = OrderItemSpecification.hasStatus(status)
                .and(OrderItemSpecification.createdBetween(startDate, endDate))
                .and(OrderItemSpecification.hasItemId(itemId));

        Page<OrderItem> orderItemPage = orderItemRepo.findAll(spec, pageable);

        if (orderItemPage.isEmpty()){
            throw new NotFoundException("Pedido no encontrado");
        }
        List<OrderItemDto> orderItemDtos = orderItemPage.getContent().stream()
                .map(entityDtoMapper::mapOrderItemToDtoPlusProductAndUser)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .orderItemList(orderItemDtos)
                .totalPage(orderItemPage.getTotalPages())
                .totalElement(orderItemPage.getTotalElements())
                .build();
    }
}
