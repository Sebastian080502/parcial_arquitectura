package com.iglesia.controller;


import com.iglesia.dto.request.EnrollmentRequest;
import com.iglesia.dto.response.EnrollmentResponse;
import com.iglesia.dto.response.ErrorResponse;

import com.iglesia.service.ChurchService;
import com.iglesia.EnrollmentStatus;
import com.iglesia.PaymentType;
import com.iglesia.entity.Church;
import com.iglesia.entity.Course;
import com.iglesia.entity.Enrollment;
import com.iglesia.entity.Payment;
import com.iglesia.entity.Person;
import com.iglesia.exception.*;
import com.iglesia.repository.CourseRepository;
import com.iglesia.repository.EnrollmentRepository;
import com.iglesia.repository.PaymentRepository;
import com.iglesia.repository.PersonRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentRepository enrollmentRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchService churchService;

    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                                PersonRepository personRepository,
                                CourseRepository courseRepository,
                                PaymentRepository paymentRepository,
                                ChurchService churchService) {
        this.enrollmentRepository = enrollmentRepository;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.paymentRepository = paymentRepository;
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public EnrollmentResponse create(@Valid @RequestBody EnrollmentRequest request) {
        Church church = churchService.getRequiredChurch();

        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new PersonNotFoundException(request.personId()));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CourseNotFoundException(request.courseId()));

        if (!person.getChurch().getId().equals(church.getId()) ||
                !course.getChurch().getId().equals(church.getId())) {
            throw new BusinessRuleException("Datos no pertenecen a la iglesia");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setPerson(person);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDIENTE);
        enrollmentRepository.save(enrollment);

        Payment payment = new Payment();
        payment.setType(PaymentType.INSCRIPCION_CURSO);
        payment.setAmount(course.getPrice());
        payment.setReferenceId(enrollment.getId());
        paymentRepository.save(payment);

        enrollment.setPaymentId(payment.getId());
        enrollmentRepository.save(enrollment);

        return EnrollmentResponse.from(enrollment, payment);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<EnrollmentResponse> list() {
        Church church = churchService.getRequiredChurch();

        return enrollmentRepository.findAllByPersonChurchId(church.getId())
                .stream()
                .map(enrollment -> {
                    Payment payment = null;
                    if (enrollment.getPaymentId() != null) {
                        payment = paymentRepository.findById(enrollment.getPaymentId()).orElse(null);
                    }
                    return EnrollmentResponse.from(enrollment, payment);
                })
                .toList();
    }

}