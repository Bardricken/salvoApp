package com.codeoftheweb.salvo.service.implementation;

import com.codeoftheweb.salvo.model.Salvo;
import com.codeoftheweb.salvo.repository.SalvoRepository;
import com.codeoftheweb.salvo.service.SalvoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalvoServiceImplement implements SalvoService {
    @Autowired
    SalvoRepository salvoRepository;

    @Override
    public Salvo saveSalvo(Salvo salvo) {
        return salvoRepository.save(salvo);
    }

    @Override
    public List<Salvo> getSalvo() {
        return salvoRepository.findAll();
    }

    @Override
    public Salvo updateSalvo(Salvo salvo) {
        return null;
    }

    @Override
    public boolean deleteSalvo(Long id) {
        return false;
    }

    @Override
    public Salvo findSalvoById(Long id) {
        return salvoRepository.findById(id).get();
    }
}
