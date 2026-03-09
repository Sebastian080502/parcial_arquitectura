package com.iglesia.service;

import com.iglesia.entity.Church;
import com.iglesia.exception.ChurchNotFoundException;
import com.iglesia.repository.ChurchRepository;

import org.springframework.stereotype.Service;

@Service
public class ChurchService {
    private final ChurchRepository churchRepository;

    public ChurchService(ChurchRepository churchRepository) {
        this.churchRepository = churchRepository;
    }


    public Church getRequiredChurch() {
        return churchRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(ChurchNotFoundException::new);
    }

    // ✨ NUEVO — verifica si ya existe una iglesia
    public boolean exists() {
        return churchRepository.count() > 0;
    }

    // ✨ NUEVO — crea y guarda la iglesia
    public Church create(String name, String address) {
        Church church = new Church();
        church.setName(name);
        church.setAddress(address);
        return churchRepository.save(church);
    }
}