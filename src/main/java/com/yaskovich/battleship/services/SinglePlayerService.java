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

    /* *
    * Codes for battleField:
    * 0 - empty point
    * 1 - part of the ship
    * 2 - space around the ship
    * 3 - shot past
    * 4 - ship damage
    * 5 - part of the sank ship
    * 6 - space around the sank ship
    * */

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

        BattleFieldModel battleFieldModel = model.getBattleFieldModel();
        List<Ship> ships = battleFieldModel.getShips();
        int[] battleField = battleFieldModel.getBattleField();

        if(model.isBotStatus()) { // if it's the bots turn
            if(model.getBotLastHits() != null && !model.getBotLastHits().isEmpty()) {
                int nextPoint = getNextPoint(model.getBotLastHits(), battleField);
                if(battleField[nextPoint] == 1) {
                    battleField[nextPoint] = 4;
                    if(isSank(nextPoint, ships, battleField)) {
                        markSankShip(nextPoint, ships, battleField);
                        model.setBotLastHits(new ArrayList<>());
                    } else {
                        model.getBotLastHits().add(nextPoint);
                    }
                } else {
                    battleField[nextPoint] = 3;
                    model.setBotStatus(false);
                }
            } else {
                int randomPoint = getRandomPoint(battleField);
                if(battleField[randomPoint] == 1) {
                    battleField[randomPoint] = 4;
                    if(isSank(randomPoint, ships, battleField)) {
                        markSankShip(randomPoint, ships, battleField);
                        model.setBotLastHits(new ArrayList<>());
                    } else {
                        model.getBotLastHits().add(randomPoint);
                    }
                } else {
                    battleField[randomPoint] = 3;
                    model.setBotStatus(false);
                }
            }
        } else { // if it's the player's turn
            if(battleField[point] == 1) {
                battleField[point] = 4;
                if(isSank(point, ships, battleField)) {
                    markSankShip(point, ships, battleField);
                }
            } else {
                battleField[point] = 3;
                model.setBotStatus(true);
            }
        }

        battleFieldModel.setShips(ships);
        battleFieldModel.setBattleField(battleField);
        model.setBattleFieldModel(battleFieldModel);
        LOGGER.debug("The method makeHit() worked");
        return model;
    }

    ////////// Helper Methods for getRandomArrangedShips() start //////////

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

    ////////// Helper Methods for getRandomArrangedShips() end //////////

    ////////// Helper Methods for makeHit(...) start //////////

    private boolean isSank(int point, List<Ship> ships, int[] battleField) {
        Ship ship = new Ship();
        for(Ship s : ships) {
            if(s.getCoordinates().contains(point)) {
                ship = s;
                break;
            }
        }
        LOGGER.debug("The method isSank(): Ship: {}", ship);
        for(int coordinate : ship.getCoordinates()) {
            if(battleField[coordinate] != 4) {
                LOGGER.debug("The method isSank() returned false");
                return false;
            }
        }
        LOGGER.debug("The method isSank() returned true");
        return true;
    }

    private void markSankShip(int point, List<Ship> ships, int[] battleField) {
        Ship ship = new Ship();
        for(Ship s : ships) {
            if(s.getCoordinates().contains(point)) {
                ship = s;
                break;
            }
        }
        LOGGER.debug("The method markSankShip(): Ship: {}", ship);
        for(int coordinate : ship.getCoordinates()) {
            battleField[coordinate] = 5;
        }
        for(int space : ship.getSpaceAround()) {
            battleField[space] = 6;
        }
        ships.remove(ship);
    }

    private int getNextPoint(List<Integer> botLastHits, int[] battleField) {
        if(botLastHits.size() == 1) {
            int nextPoint = botLastHits.get(0);
            if((nextPoint+1) < 100 && battleField[nextPoint+1] < 3) { return (nextPoint+1); }
            if((nextPoint+10) < 100 && battleField[nextPoint+10] < 3) { return (nextPoint+10); }
            if((nextPoint-1) > 0 && battleField[nextPoint-1] < 3) { return (nextPoint-1); }
            if((nextPoint-10) > 0 && battleField[nextPoint-10] < 3) { return (nextPoint-10); }
        } else {
            if(botLastHits.get(0) - botLastHits.get(1) == 1) {
                return (botLastHits.get(botLastHits.size()-1)-1);
            }
            if(botLastHits.get(0) - botLastHits.get(1) == -1) {
                return (botLastHits.get(botLastHits.size()-1)+1);
            }
            if(botLastHits.get(0) - botLastHits.get(1) == 10) {
                return (botLastHits.get(botLastHits.size()-1)-10);
            }
            if(botLastHits.get(0) - botLastHits.get(1) == -10) {
                return (botLastHits.get(botLastHits.size()-1)+10);
            }
        }
        LOGGER.debug("The method getNextPoint() worked");
        throw new RuntimeException("Something wrong with next point");
    }

    private int getRandomPoint(int[] battleField) {
        Random random = new Random();
        int randomPoint = random.nextInt(100);
        while(battleField[randomPoint] > 2) {
            randomPoint = random.nextInt(100);
        }
        LOGGER.debug("The method getRandomPoint() returned " + randomPoint);
        return randomPoint;
    }

    ////////// Helper Methods for makeHit(...) end //////////
}
