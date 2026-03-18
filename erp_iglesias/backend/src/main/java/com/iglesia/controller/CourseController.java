package com.iglesia.controller;

import com.iglesia.service.CourseService;
import com.iglesia.dto.request.CourseRequest;
import com.iglesia.dto.response.CourseResponse;
import com.iglesia.entity.Course;
import com.iglesia.exception.CourseNotFoundException;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CourseResponse create(@Valid @RequestBody CourseRequest request) {
        return courseService.create(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<CourseResponse> list() {
        return courseService.listAll();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public CourseResponse getById(@PathVariable Long id) {

        System.out.println("➡️ Ejecutando getById con id: " + id);
        return courseService.getById(id);
    }
}