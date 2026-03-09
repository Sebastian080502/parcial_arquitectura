package com.iglesia.controller;

import com.iglesia.service.EnrollmentService;
import com.iglesia.dto.request.EnrollmentRequest;
import com.iglesia.dto.response.EnrollmentResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public EnrollmentResponse create(@Valid @RequestBody EnrollmentRequest request) {
        return enrollmentService.create(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<EnrollmentResponse> list() {
        return enrollmentService.listAll();
    }
}