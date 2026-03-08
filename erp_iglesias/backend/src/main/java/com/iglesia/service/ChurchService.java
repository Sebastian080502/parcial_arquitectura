package com.iglesia.service;

import com.iglesia.Church;
import com.iglesia.ChurchRepository;
import com.iglesia.exception.ChurchNotFoundException;
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
}

