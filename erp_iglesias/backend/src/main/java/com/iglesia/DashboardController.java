package com.iglesia;

import com.iglesia.service.ChurchService;
import com.iglesia.Church;
import com.iglesia.PaymentStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public DashboardController(ChurchService churchService,
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
        Church church = churchService.getRequiredChurch();

        long totalPeople = personRepository.countByChurchId(church.getId());
        long activeCourses = courseRepository.countByChurchIdAndActiveTrue(church.getId());

        YearMonth month = YearMonth.now();
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();
        long offeringsMonth = offeringRepository.countByCreatedAtBetween(start, end);

        long pendingPayments = paymentRepository.countByStatus(PaymentStatus.INICIADO);

        return new DashboardResponse(totalPeople, activeCourses, offeringsMonth, pendingPayments);
    }

    // DTO interno
    public record DashboardResponse(long totalPeople, long activeCourses, long offeringsMonth, long pendingPayments) {}
}