package com.yaskovich.battleship.services;

import com.yaskovich.battleship.entity.Ship;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.SinglePlayerGameModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SinglePlayerService {

    private final Integer[] sizeOfShips = {4,3,3,2,2,2,1,1,1,1};
    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePlayerService.class);

    public BattleFieldModel getRandomArrangedShips() {
        List<Ship> ships = new ArrayList<>();
        int[] battleField = new int[100];

        List<Integer> sizeOfShipsList = new ArrayList<>();
        Collections.addAll(sizeOfShipsList, sizeOfShips);

        while(sizeOfShipsList.size() > 0) {
            Integer shipSize = sizeOfShipsList.get(0);
            Random random = new Random();
            int point = random.nextInt(100);
            boolean orientation = random.nextBoolean(); // false = horizontal, true = vertical
            if(checkNewShip(point, shipSize, orientation, battleField)) {
                Ship newShip = createNewShip(point, shipSize, orientation);
                ships.add(newShip);
                addNewShipToBattleField(newShip, battleField);
                sizeOfShipsList.remove(0);
            }
        }
        LOGGER.debug("The method getRandomArrangedShips() worked");
        return new BattleFieldModel(ships, battleField);
    }

    public SinglePlayerGameModel makeHit(Integer point, SinglePlayerGameModel model) {
        if(point < 0 || point > 99) {
            throw new RuntimeException("The point must be from 0 to 99");
        }
        if(model.isBotStatus()) { // if it's the bot's turn
            if(model.getBotLastHits() != null && !model.getBotLastHits().isEmpty()) {

            } else {
                makeRandomHit(model);
            }
        } else { // if it's the player's turn

        }
        return model;
    }

    /////////////////////////////////////////////////////////////////////////////////

    private boolean checkNewShip(int point, int shipSize, boolean orientation, int[] battleField) {
        if(!orientation && (point/10 != (point+shipSize-1)/10)) {
            return false;
        }
        int step = orientation ? 10 : 1;
        for(int i = 0; i < shipSize; i++) {
            if(point >= battleField.length || battleField[point] != 0) {
                return false;
            }
            point = point + step;
        }
        LOGGER.debug("The method checkNewShip() worked");
        return true;
    }

    private Ship createNewShip(int point, int shipSize, boolean orientation) {
        Set<Integer> coordinates = new TreeSet<>();
        int step = orientation ? 10 : 1;
        for(int i = 0; i < shipSize; i++) {
            coordinates.add(point);
            point = point + step;
        }
        LOGGER.debug("The method createNewShip() worked");
        return new Ship(coordinates, createSpaceAround(coordinates));
    }

    private Set<Integer> createSpaceAround(Set<Integer> coordinates) {
        Set<Integer> space = new TreeSet<>();
        for(Integer i : coordinates) {
            if(i-1 >= 0 && (i-1)/10 == i/10 && !coordinates.contains(i-1)) {space.add(i-1);}
            if(i+1 < 100 && (i+1)/10 == i/10 && !coordinates.contains(i+1)) {space.add(i+1);}
            if(i-10 >= 0 && !coordinates.contains(i-10)) {space.add(i-10);}
            if(i+10 < 100 && !coordinates.contains(i+10)) {space.add(i+10);}
            if(i-9 >= 0 && ((i-9)/10)+1 == i/10) {space.add(i-9);}
            if(i+9 < 100 && ((i+9)/10)-1 == i/10) {space.add(i+9);}
            if(i-11 >= 0 && ((i-11)/10)+1 == i/10) {space.add(i-11);}
            if(i+11 < 100 && ((i+11)/10)-1 == i/10) {space.add(i+11);}
        }
        LOGGER.debug("The method createSpaceAround() worked");
        return space;
    }

    private void addNewShipToBattleField(Ship ship, int[] battleField) {
        for(Integer point : ship.getCoordinates()) {
            battleField[point] = 1;
        }
        for(Integer point : ship.getSpaceAround()) {
            battleField[point] = 2;
        }
        LOGGER.debug("The method addNewShipToBattleField() worked");
    }

    private SinglePlayerGameModel makeRandomHit(SinglePlayerGameModel model) {
        BattleFieldModel battleFieldModel = model.getBattleFieldModel();
        int[] battleField = battleFieldModel.getBattleField();
        List<Ship> ships = battleFieldModel.getShips();

        Random random = new Random();
        int randomPoint = random.nextInt(100);
        boolean checkResult = false;

        while(checkResult) {
            // bot hit or sank the ship
            if(battleField[randomPoint] == 1) {
                updateBattleFieldModelIfBotHitOrSank(ships, randomPoint, battleField, model);
                checkResult = true;
            }
            // bot missed
            else if(battleField[randomPoint] == 0 || battleField[randomPoint] == 2) {
                battleField[randomPoint] = 3;
                model.setBotStatus(false);
                checkResult = true;
            }
            // already shot at this point
            else {
                randomPoint = random.nextInt(100);
            }
        }
        battleFieldModel.setBattleField(battleField);
        battleFieldModel.setShips(ships);
        model.setBattleFieldModel(battleFieldModel);
        return model;
    }

    private void updateBattleFieldModelIfBotHitOrSank(
            List<Ship> ships, int randomPoint, int[] battleField, SinglePlayerGameModel model) {
        for(Ship ship : ships) {
            if(ship.getCoordinates().contains(randomPoint)) {
                if(isSank(ship.getCoordinates(), battleField)) {
                    markSankShip(ship, battleField);
                    ships.remove(ship);
                } else {
                    battleField[randomPoint] = 3;
                    model.getBotLastHits().add(randomPoint);
                }
            }
        }
    }

    private boolean isSank(Set<Integer> coordinates, int[] battleField) {
        for(int coordinate : coordinates) {
            if (battleField[coordinate] != 3) {
                return false;
            }
        }
        return true;
    }

    private void  markSankShip(Ship ship, int[] battleField) {
        for(int coordinate : ship.getCoordinates()) {
            battleField[coordinate] = 4;
        }
        for(int space : ship.getSpaceAround()) {
            battleField[space] = 5;
        }
    }
}
