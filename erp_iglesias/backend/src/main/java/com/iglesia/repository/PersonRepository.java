package com.iglesia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iglesia.entity.Person;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findAllByChurchId(Long churchId);
    long countByChurchId(Long churchId);
}
