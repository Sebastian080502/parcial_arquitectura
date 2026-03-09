package com.iglesia.controller;

import com.iglesia.dto.request.ChurchRequest;
import com.iglesia.dto.response.ChurchResponse;

import com.iglesia.service.ChurchService;
import com.iglesia.entity.Church;
import com.iglesia.exception.BusinessRuleException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/church")
public class ChurchController {
    private final ChurchService churchService;

    public ChurchController(ChurchService churchService) {
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ChurchResponse create(@Valid @RequestBody ChurchRequest request) {
        if (churchService.exists()) {
            throw new BusinessRuleException("Ya existe una iglesia registrada");
        }
        Church church = churchService.create(request.name(), request.address());
        return ChurchResponse.from(church);
    }

    @GetMapping
    public ChurchResponse get() {
        Church church = churchService.getRequiredChurch(); // lanza ChurchNotFoundException
        return ChurchResponse.from(church);
    }

   
}