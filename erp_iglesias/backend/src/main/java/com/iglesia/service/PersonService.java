package com.iglesia.service;

import com.iglesia.entity.Church;
import com.iglesia.entity.Person;
import com.iglesia.repository.PersonRepository;
import com.iglesia.dto.request.PersonRequest;
import com.iglesia.dto.response.PersonResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final ChurchService churchService;

    public PersonService(PersonRepository personRepository, ChurchService churchService) {
        this.personRepository = personRepository;
        this.churchService = churchService;
    }

    public PersonResponse create(PersonRequest request) {
        Church church = churchService.getRequiredChurch();

        Person person = new Person();
        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        person.setDocument(request.document());
        person.setPhone(request.phone());
        person.setEmail(request.email());
        person.setChurch(church);

        personRepository.save(person);
        return PersonResponse.from(person);
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> listAll() {
        Church church = churchService.getRequiredChurch();

        return personRepository.findAllByChurchId(church.getId())
                .stream()
                .map(PersonResponse::from)
                .toList();
    }

    public PersonResponse findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}