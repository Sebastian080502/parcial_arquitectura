package com.iglesia.service;

import com.iglesia.entity.Church;
import com.iglesia.entity.Offering;
import com.iglesia.entity.Payment;
import com.iglesia.entity.Person;
import com.iglesia.OfferingStatus;
import com.iglesia.PaymentType;
import com.iglesia.exception.BusinessRuleException;
import com.iglesia.exception.PersonNotFoundException;
import com.iglesia.repository.OfferingRepository;
import com.iglesia.repository.PaymentRepository;
import com.iglesia.repository.PersonRepository;
import com.iglesia.dto.request.OfferingRequest;
import com.iglesia.dto.response.OfferingResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final PersonRepository personRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchService churchService;

    public OfferingService(OfferingRepository offeringRepository,
                           PersonRepository personRepository,
                           PaymentRepository paymentRepository,
                           ChurchService churchService) {
        this.offeringRepository = offeringRepository;
        this.personRepository = personRepository;
        this.paymentRepository = paymentRepository;
        this.churchService = churchService;
    }

    public OfferingResponse create(OfferingRequest request) {
        Church church = churchService.getRequiredChurch();

        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new PersonNotFoundException(request.personId()));

        if (!person.getChurch().getId().equals(church.getId())) {
            throw new BusinessRuleException("La persona no pertenece a la iglesia actual");
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
    }

    @Transactional(readOnly = true)
    public java.util.List<OfferingResponse> listAll() {
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
    }
}