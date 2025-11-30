package com.restaurant.MesondelDesierto.specification;

import com.restaurant.MesondelDesierto.entity.OrderItem;
import com.restaurant.MesondelDesierto.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderItemSpecification {
    /**Specification del filtro de las ordenes de items por estado */
    public static Specification<OrderItem> hasStatus(OrderStatus status){
        return ((root, query, criteriaBuilder) ->
                status != null ? criteriaBuilder.equal(root.get("status"), status) : null);

    }

    /**Specification del filtro de las ordenes, rango */
    public static Specification<OrderItem> createdBetween(LocalDateTime startDate, LocalDateTime endDate){
        return ((root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null){
                return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            }else{
                return null;
            }
        });
    }

    /** Genera specification del filtro orderitems por id*/
    public static Specification<OrderItem> hasItemId(Long itemId){
        return ((root, query, criteriaBuilder) ->
                itemId != null ? criteriaBuilder.equal(root.get("id"), itemId) : null);
    }
}
