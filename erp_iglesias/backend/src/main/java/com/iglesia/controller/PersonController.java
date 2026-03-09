package com.iglesia.controller;

import com.iglesia.dto.request.PersonRequest;
import com.iglesia.dto.response.PersonResponse;

import com.iglesia.service.ChurchService;
import com.iglesia.entity.Church;
import com.iglesia.entity.Person;
import com.iglesia.repository.PersonRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/people")
public class PersonController {
    private final PersonRepository personRepository;
    private final ChurchService churchService;

    public PersonController(PersonRepository personRepository, ChurchService churchService) {
        this.personRepository = personRepository;
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public PersonResponse create(@Valid @RequestBody PersonRequest request) {
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<PersonResponse> list() {
        Church church = churchService.getRequiredChurch();

        return personRepository.findAllByChurchId(church.getId())
                .stream()
                .map(PersonResponse::from)
                .toList();
    }

}