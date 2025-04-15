package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.repository.ActRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActService {

    @Autowired
    private ActRepository actRepository;

    public List<Act> getAllActs() {
        return actRepository.findAll();
    }
}
