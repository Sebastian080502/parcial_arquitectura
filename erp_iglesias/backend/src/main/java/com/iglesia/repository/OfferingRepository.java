package com.iglesia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iglesia.entity.Offering;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findAllByPersonChurchId(Long churchId);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
