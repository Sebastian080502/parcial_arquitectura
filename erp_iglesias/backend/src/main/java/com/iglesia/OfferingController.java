package com.iglesia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import com.iglesia.service.ChurchService;
import com.iglesia.OfferingController.OfferingRequest;
import com.iglesia.OfferingController.OfferingResponse;
import com.iglesia.exception.ChurchNotFoundException;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {
    private final OfferingRepository offeringRepository;
    private final PersonRepository personRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchService churchService; // Nuevo

    public OfferingController(OfferingRepository offeringRepository,
            PersonRepository personRepository,
            PaymentRepository paymentRepository,
            ChurchService churchService) {

        this.offeringRepository = offeringRepository;
        this.personRepository = personRepository;
        this.paymentRepository = paymentRepository;
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public OfferingResponse create(@RequestBody OfferingRequest request) {
        try {
            Church church = churchService.getRequiredChurch();

            Person person = personRepository.findById(request.personId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

            if (!person.getChurch().getId().equals(church.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Persona no pertenece a la iglesia");
            }

            Offering offering = new Offering();
            offering.setPerson(person);
            offering.setAmount(request.amount());
            offering.setConcept(request.concept());
            offering.setStatus(OfferingStatus.PENDIENTE);
            offeringRepository.save(offering);

            Payment payment = new Payment();
            payment.setType(PaymentType.OFRENDA);
            payment.setAmount(request.amount());
            payment.setReferenceId(offering.getId());
            paymentRepository.save(payment);

            offering.setPaymentId(payment.getId());
            offeringRepository.save(offering);

            return OfferingResponse.from(offering, payment);

        } catch (ChurchNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<OfferingResponse> list() {
        try {
            Church church = churchService.getRequiredChurch();

            return offeringRepository.findAllByPersonChurchId(church.getId())
                    .stream()
                    .map(offering -> {
                        Payment payment = null;
                        if (offering.getPaymentId() != null) {
                            payment = paymentRepository.findById(offering.getPaymentId()).orElse(null);
                        }
                        return OfferingResponse.from(offering, payment);
                    })
                    .toList();
        } catch (ChurchNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero");
        }
    }

    public record OfferingRequest(
            @NotNull Long personId,
            @NotNull BigDecimal amount,
            @NotBlank String concept) {
    }

    public record OfferingResponse(
            Long id,
            Long personId,
            String personName,
            String concept,
            String amount,
            String status,
            Long paymentId,
            String paymentStatus) {
        public static OfferingResponse from(Offering offering, Payment payment) {
            String personName = offering.getPerson().getFirstName() + " " + offering.getPerson().getLastName();
            String paymentStatus = payment == null ? null : payment.getStatus().name();
            return new OfferingResponse(
                    offering.getId(),
                    offering.getPerson().getId(),
                    personName,
                    offering.getConcept(),
                    offering.getAmount().toPlainString(),
                    offering.getStatus().name(),
                    offering.getPaymentId(),
                    paymentStatus);
        }
    }
}
