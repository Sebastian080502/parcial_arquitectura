package com.iglesia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iglesia.entity.Church;

public interface ChurchRepository extends JpaRepository<Church, Long> {
    boolean existsByNameIgnoreCase(String name);
}
