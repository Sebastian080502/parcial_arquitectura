package com.iglesia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iglesia.entity.Enrollment;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findAllByPersonChurchId(Long churchId);
    List<Enrollment> findAllByPersonId(Long personId);
}
