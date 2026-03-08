package com.iglesia;

import com.iglesia.service.ChurchService; // Importar el servicio
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/church")
public class ChurchController {
    private final ChurchRepository churchRepository;
    private final ChurchService churchService; // Nuevo

    public ChurchController(ChurchRepository churchRepository, ChurchService churchService) {
        this.churchRepository = churchRepository;
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ChurchResponse create(@RequestBody ChurchRequest request) {
        if (churchRepository.count() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una iglesia registrada");
        }
        Church church = new Church();
        church.setName(request.name());
        church.setAddress(request.address());
        churchRepository.save(church);
        return ChurchResponse.from(church);
    }

    @GetMapping
    public ChurchResponse get() {
        // Usar el servicio
        Church church = churchService.getRequiredChurch();
        return ChurchResponse.from(church);
    }

    public record ChurchRequest(
        @NotBlank String name,
        String address
    ) {}

    public record ChurchResponse(
        Long id,
        String name,
        String address
    ) {
        public static ChurchResponse from(Church church) {
            return new ChurchResponse(church.getId(), church.getName(), church.getAddress());
        }
    }
}
