package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.model.Ship;

import java.util.List;

public interface ShipService {
    Ship saveShip(Ship ship);

    List<Ship> getShip();

    Ship updateShip(Ship ship);

    boolean deleteShip(Long id);

    Ship findShipById(Long id);
}
