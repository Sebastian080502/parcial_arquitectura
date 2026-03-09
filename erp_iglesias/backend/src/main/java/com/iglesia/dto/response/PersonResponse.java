package com.iglesia.dto.response;

import com.iglesia.entity.Person;

public record PersonResponse(
    Long id,
    String firstName,
    String lastName,
    String document,
    String phone,
    String email
) {
    public static PersonResponse from(Person person) {
        return new PersonResponse(
            person.getId(),
            person.getFirstName(),
            person.getLastName(),
            person.getDocument(),
            person.getPhone(),
            person.getEmail()
        );
    }
}