package com.iglesia;


import com.iglesia.exception.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OfferingRepository offeringRepository;

    public PaymentController(PaymentRepository paymentRepository,
                             EnrollmentRepository enrollmentRepository,
                             OfferingRepository offeringRepository) {
        this.paymentRepository = paymentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.offeringRepository = offeringRepository;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<PaymentResponse> list(@RequestParam(name = "status", required = false) PaymentStatus status) {
        List<Payment> payments = (status == null) ? paymentRepository.findAll() : paymentRepository.findAllByStatus(status);
        return payments.stream().map(PaymentResponse::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/confirm")
    public PaymentResponse confirm(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        payment.setStatus(PaymentStatus.CONFIRMADO);
        paymentRepository.save(payment);

        if (payment.getType() == PaymentType.INSCRIPCION_CURSO) {
            Enrollment enrollment = enrollmentRepository.findById(payment.getReferenceId())
                    .orElseThrow(() -> new EnrollmentNotFoundException(payment.getReferenceId()));
            enrollment.setStatus(EnrollmentStatus.PAGADA);
            enrollmentRepository.save(enrollment);
        } else if (payment.getType() == PaymentType.OFRENDA) {
            Offering offering = offeringRepository.findById(payment.getReferenceId())
                    .orElseThrow(() -> new OfferingNotFoundException(payment.getReferenceId()));
            offering.setStatus(OfferingStatus.REGISTRADA);
            offeringRepository.save(offering);
        }

        return PaymentResponse.from(payment);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/fail")
    public PaymentResponse fail(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (payment.getStatus() == PaymentStatus.CONFIRMADO) {
            throw new BusinessRuleException("El pago ya fue confirmado");
        }

        payment.setAttempts(payment.getAttempts() + 1);
        payment.setStatus(PaymentStatus.FALLIDO);
        paymentRepository.save(payment);

        return PaymentResponse.from(payment);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/retry")
    public PaymentResponse retry(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (payment.getStatus() != PaymentStatus.FALLIDO) {
            throw new BusinessRuleException("Solo se reintenta un pago fallido");
        }

        if (payment.getAttempts() >= 3) {
            throw new BusinessRuleException("Se superó el máximo de reintentos");
        }

        payment.setStatus(PaymentStatus.INICIADO);
        paymentRepository.save(payment);

        return PaymentResponse.from(payment);
    }

    // DTO interno
    public record PaymentResponse(Long id, String type, String status, String amount, int attempts, Long referenceId) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                    payment.getId(),
                    payment.getType().name(),
                    payment.getStatus().name(),
                    payment.getAmount().toPlainString(),
                    payment.getAttempts(),
                    payment.getReferenceId()
            );
        }
    }
}