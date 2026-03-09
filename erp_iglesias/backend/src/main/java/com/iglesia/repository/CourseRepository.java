package com.iglesia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iglesia.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByChurchId(Long churchId);
    long countByChurchIdAndActiveTrue(Long churchId);
}
