package com.iglesia.service;

import com.iglesia.entity.Payment;
import com.iglesia.entity.Enrollment;
import com.iglesia.entity.Offering;
import com.iglesia.PaymentStatus;
import com.iglesia.EnrollmentStatus;
import com.iglesia.OfferingStatus;
import com.iglesia.PaymentType;
import com.iglesia.exception.BusinessRuleException;
import com.iglesia.exception.PaymentNotFoundException;
import com.iglesia.exception.EnrollmentNotFoundException;
import com.iglesia.exception.OfferingNotFoundException;
import com.iglesia.repository.PaymentRepository;
import com.iglesia.repository.EnrollmentRepository;
import com.iglesia.repository.OfferingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OfferingRepository offeringRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          EnrollmentRepository enrollmentRepository,
                          OfferingRepository offeringRepository) {
        this.paymentRepository = paymentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.offeringRepository = offeringRepository;
    }

    @Transactional(readOnly = true)
    public List<Payment> listAll(PaymentStatus status) {
        if (status == null) {
            return paymentRepository.findAll();
        }
        return paymentRepository.findAllByStatus(status);
    }

    public Payment confirm(Long id) {
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

        return payment;
    }

    public Payment fail(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (payment.getStatus() == PaymentStatus.CONFIRMADO) {
            throw new BusinessRuleException("El pago ya fue confirmado");
        }

        payment.setAttempts(payment.getAttempts() + 1);
        payment.setStatus(PaymentStatus.FALLIDO);
        return paymentRepository.save(payment);
    }

    public Payment retry(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (payment.getStatus() != PaymentStatus.FALLIDO) {
            throw new BusinessRuleException("Solo se reintenta un pago fallido");
        }

        if (payment.getAttempts() >= 3) {
            throw new BusinessRuleException("Se superó el máximo de reintentos");
        }

        payment.setStatus(PaymentStatus.INICIADO);
        return paymentRepository.save(payment);
    }
}