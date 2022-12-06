package com.yaskovich.battleship.services;

import com.yaskovich.battleship.entity.Ship;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.PlayerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GameModelObjectMother {

    public static GameModel getGameModel() {
        int[] battleField = new int[100];
        Ship ship = new Ship(Set.of(0,1,2,3), Set.of(4,10,11,12,13,14));
        for(int coordinate : ship.getCoordinates()) {
            battleField[coordinate] = 1;
        }
        for(int spaceAround : ship.getSpaceAround()) {
            battleField[spaceAround] = 2;
        }
        List<Ship> ships = new ArrayList<>();
        ships.add(ship);
        PlayerModel playerModel = new PlayerModel(UUID.randomUUID(), "Name", ships, battleField);
        PlayerModel botModel = new PlayerModel(UUID.randomUUID(), "Bot", ships, battleField);
        botModel.setPlayerId(UUID.randomUUID());
        return new GameModel(UUID.randomUUID(), playerModel, botModel, new ArrayList<>());
    }
}
