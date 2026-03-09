package com.iglesia.service;

import com.iglesia.entity.Church;
import com.iglesia.entity.Course;
import com.iglesia.entity.Enrollment;
import com.iglesia.entity.Payment;
import com.iglesia.entity.Person;
import com.iglesia.EnrollmentStatus;
import com.iglesia.PaymentType;
import com.iglesia.exception.BusinessRuleException;
import com.iglesia.exception.CourseNotFoundException;
import com.iglesia.exception.PersonNotFoundException;
import com.iglesia.repository.CourseRepository;
import com.iglesia.repository.EnrollmentRepository;
import com.iglesia.repository.PaymentRepository;
import com.iglesia.repository.PersonRepository;
import com.iglesia.dto.request.EnrollmentRequest;
import com.iglesia.dto.response.EnrollmentResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchService churchService;  // Reutilizamos ADR-002

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
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

    /**
     * Crea una nueva inscripción y su pago asociado.
     */
    public EnrollmentResponse create(EnrollmentRequest request) {
        // 1. Obtener la iglesia actual (lanza ChurchNotFoundException si no existe)
        Church church = churchService.getRequiredChurch();

        // 2. Buscar persona y curso
        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new PersonNotFoundException(request.personId()));

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CourseNotFoundException(request.courseId()));

        // 3. Validar que pertenezcan a la iglesia
        if (!person.getChurch().getId().equals(church.getId()) ||
            !course.getChurch().getId().equals(church.getId())) {
            throw new BusinessRuleException("La persona o el curso no pertenecen a la iglesia actual");
        }

        // 4. Crear la inscripción
        Enrollment enrollment = new Enrollment();
        enrollment.setPerson(person);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDIENTE);
        enrollmentRepository.save(enrollment);

        // 5. Crear el pago asociado
        Payment payment = new Payment();
        payment.setType(PaymentType.INSCRIPCION_CURSO);
        payment.setAmount(course.getPrice());
        payment.setReferenceId(enrollment.getId());  // Referencia débil (mejoraremos en ADR-004)
        paymentRepository.save(payment);

        // 6. Actualizar la inscripción con el ID del pago
        enrollment.setPaymentId(payment.getId());
        enrollmentRepository.save(enrollment);

        // 7. Retornar respuesta
        return EnrollmentResponse.from(enrollment, payment);
    }

    /**
     * Lista todas las inscripciones de la iglesia actual.
     */
    @Transactional(readOnly = true)
    public java.util.List<EnrollmentResponse> listAll() {
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