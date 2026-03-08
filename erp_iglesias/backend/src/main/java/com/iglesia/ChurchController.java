package com.iglesia;

import com.iglesia.service.ChurchService;
import com.iglesia.Church;
import com.iglesia.exception.BusinessRuleException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    // DTOs internos
    public record ChurchRequest(@NotBlank String name, String address) {}
    public record ChurchResponse(Long id, String name, String address) {
        public static ChurchResponse from(Church church) {
            return new ChurchResponse(church.getId(), church.getName(), church.getAddress());
        }
    }
}