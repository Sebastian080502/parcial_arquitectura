package com.iglesia;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.iglesia.service.ChurchService;
import com.iglesia.exception.ChurchNotFoundException;
import com.iglesia.Church;

import java.time.LocalDateTime;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ChurchService churchService;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final OfferingRepository offeringRepository;
    private final PaymentRepository paymentRepository;

    public DashboardController(
            ChurchService churchService,
            PersonRepository personRepository,
            CourseRepository courseRepository,
            OfferingRepository offeringRepository,
            PaymentRepository paymentRepository) {
        this.churchService = churchService;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.offeringRepository = offeringRepository;
        this.paymentRepository = paymentRepository;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public DashboardResponse get() {
        try {
            // 1. OBTENER LA IGLESIA usando ChurchService
            Church church = churchService.getRequiredChurch();
            
            // 2. CALCULAR MÉTRICAS usando el ID de la iglesia
            long totalPeople = personRepository.countByChurchId(church.getId());
            long activeCourses = courseRepository.countByChurchIdAndActiveTrue(church.getId());

            YearMonth month = YearMonth.now();
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();
            long offeringsMonth = offeringRepository.countByCreatedAtBetween(start, end);

            long pendingPayments = paymentRepository.countByStatus(PaymentStatus.INICIADO);

            // 3. RETORNAR RESPUESTA
            return new DashboardResponse(
                totalPeople, 
                activeCourses, 
                offeringsMonth, 
                pendingPayments
            );
            
        } catch (ChurchNotFoundException e) {
            // 4. MANEJAR ERROR si no hay iglesia
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Debe registrar una iglesia primero"
            );
        }
    }

    public record DashboardResponse(
        long totalPeople,
        long activeCourses,
        long offeringsMonth,
        long pendingPayments
    ) {}
}