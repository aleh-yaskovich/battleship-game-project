package com.yaskovich.battleship.services.integration;

import com.yaskovich.battleship.entity.Ship;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.SinglePlayerGameModel;
import com.yaskovich.battleship.services.SinglePlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SinglePlayerServiceIT {

    @Autowired
    private SinglePlayerService service;

    @Test
    void shouldReturnBattleFieldModel() {
        BattleFieldModel actual = service.getRandomArrangedShips();
        assertNotNull(actual);
        assertNotNull(actual.getBattleField());
        assertNotNull(actual.getShips());
        assertEquals(actual.getBattleField().length, 100);
        assertEquals(actual.getShips().size(), 10);
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenPlayerHit() {
        Random random = new Random();
        Integer point = random.nextInt(100);
        BattleFieldModel battleFieldModel = service.getRandomArrangedShips();

        assertNotNull(battleFieldModel);
        assertTrue(battleFieldModel.getBattleField()[point] <=2);

        SinglePlayerGameModel before = new SinglePlayerGameModel();
        before.setBattleFieldModel(battleFieldModel);
        assertFalse(before.isBotStatus());

        SinglePlayerGameModel actual = service.makeHit(point, before);
        assertNotNull(actual);
        assertTrue(actual.getBattleFieldModel().getBattleField()[point] > 2);
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenPlayerHitAndShipSank() {
        BattleFieldModel battleFieldModel = service.getRandomArrangedShips();
        assertNotNull(battleFieldModel);

        List<Ship> ships = battleFieldModel.getShips();
        assertEquals(ships.size(), 10);

        Ship ship = ships.get(ships.size()-1);
        List<Integer> coordinates = new ArrayList<>(ship.getCoordinates());
        List<Integer> spaceAround = new ArrayList<>(ship.getSpaceAround());
        assertEquals(coordinates.size(), 1);

        Integer point = coordinates.get(0);
        SinglePlayerGameModel before = new SinglePlayerGameModel();
        before.setBattleFieldModel(battleFieldModel);
        assertFalse(before.isBotStatus());

        SinglePlayerGameModel actual = service.makeHit(point, before);
        assertNotNull(actual);
        assertEquals(actual.getBattleFieldModel().getBattleField()[point], 5);
        assertEquals(actual.getBattleFieldModel().getShips().size(), 9);
        for(int i : spaceAround) {
            assertEquals(actual.getBattleFieldModel().getBattleField()[i], 6);
        }
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenBotGetRandomHit() {
        BattleFieldModel battleFieldModel = service.getRandomArrangedShips();
        assertNotNull(battleFieldModel);
        List<Integer> checkHits = new ArrayList<>();
        for(int i : battleFieldModel.getBattleField()) {
            if(i > 2) {
                checkHits.add(i);
            }
        }
        assertEquals(checkHits.size(), 0);
        SinglePlayerGameModel before = new SinglePlayerGameModel();
        before.setBattleFieldModel(battleFieldModel);
        before.setBotStatus(true);
        before.setBotLastHits(new ArrayList<>());
        SinglePlayerGameModel actual = service.makeHit(0, before);
        assertNotNull(actual);
        for(int i : actual.getBattleFieldModel().getBattleField()) {
            if(i > 2) {
                checkHits.add(i);
            }
        }
        assertTrue(checkHits.size() > 0);
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenBotGetNextHit() {
        BattleFieldModel battleFieldModel = service.getRandomArrangedShips();
        assertNotNull(battleFieldModel);
        List<Ship> ships = battleFieldModel.getShips();
        assertEquals(ships.size(), 10);
        Ship ship = ships.get(0);
        assertEquals(ship.getCoordinates().size(), 4);

        List<Integer> coordinates = new ArrayList<>(ship.getCoordinates());
        Integer point = coordinates.get(0);
        battleFieldModel.getBattleField()[point] = 4;

        SinglePlayerGameModel before = new SinglePlayerGameModel();
        before.setBattleFieldModel(battleFieldModel);
        before.setBotStatus(true);
        before.setBotLastHits(List.of(point));

        SinglePlayerGameModel actual = service.makeHit(0, before);
        assertNotNull(actual);
        assertTrue(actual.getBattleFieldModel().getBattleField()[point+1] == 3 ||
                actual.getBattleFieldModel().getBattleField()[point+1] == 4);
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenBotGetNextHitAndShipSank() {
        BattleFieldModel battleFieldModel = service.getRandomArrangedShips();
        assertNotNull(battleFieldModel);
        List<Ship> ships = battleFieldModel.getShips();
        assertEquals(ships.size(), 10);
        Ship ship = ships.get(1);
        assertEquals(ship.getCoordinates().size(), 3);

        List<Integer> coordinates = new ArrayList<>(ship.getCoordinates());
        List<Integer> spaceAround = new ArrayList<>(ship.getSpaceAround());
        battleFieldModel.getBattleField()[coordinates.get(0)] = 4;
        battleFieldModel.getBattleField()[coordinates.get(1)] = 4;

        SinglePlayerGameModel before = new SinglePlayerGameModel();
        before.setBattleFieldModel(battleFieldModel);
        before.setBotStatus(true);
        before.setBotLastHits(List.of(coordinates.get(0), coordinates.get(1)));

        SinglePlayerGameModel actual = service.makeHit(0, before);
        assertNotNull(actual);
        BattleFieldModel actualBattleFieldModel = actual.getBattleFieldModel();
        assertEquals(actualBattleFieldModel.getShips().size(), 9);
        for(int coordinate : coordinates) {
            assertEquals(actualBattleFieldModel.getBattleField()[coordinate], 5);
        }
        for(int space : spaceAround) {
            assertEquals(actualBattleFieldModel.getBattleField()[space], 6);
        }
    }
}
