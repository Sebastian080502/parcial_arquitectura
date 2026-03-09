package com.iglesia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iglesia.PaymentStatus;
import com.iglesia.entity.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStatus(PaymentStatus status);
    long countByStatus(PaymentStatus status);
}
