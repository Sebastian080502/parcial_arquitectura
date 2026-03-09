package com.iglesia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iglesia.entity.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
