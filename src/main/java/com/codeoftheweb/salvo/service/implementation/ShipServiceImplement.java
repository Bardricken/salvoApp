package com.codeoftheweb.salvo.service.implementation;

import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.repository.ShipRepository;
import com.codeoftheweb.salvo.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipServiceImplement implements ShipService {
    @Autowired
    private ShipRepository shipRepository;

    @Override
    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public List<Ship> getShip() {
        return shipRepository.findAll();
    }

    @Override
    public Ship updateShip(Ship game) {
        return null;
    }

    @Override
    public boolean deleteShip(Long id) {
        return false;
    }

    @Override
    public Ship findShipById(Long id) {
        return shipRepository.findById(id).get();
    }
}
