package com.restaurant.MesondelDesierto.repository;

import com.restaurant.MesondelDesierto.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(Long orderId);
}
