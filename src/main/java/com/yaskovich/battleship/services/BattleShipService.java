package com.yaskovich.battleship.services;

import com.yaskovich.battleship.entity.Ship;
import com.yaskovich.battleship.entity.kafka.SavingGame;
import com.yaskovich.battleship.exceptions.handler.GameModelException;
import com.yaskovich.battleship.models.*;
import org.slf4j.Logger;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Data
@AllArgsConstructor
@Service
public class BattleShipService {

    /**
     * Codes for battleField:
     * 0 - empty point
     * 1 - part of the ship
     * 2 - space around the ship
     * 3 - shot past
     * 4 - ship damage
     * 5 - part of the sank ship
     * 6 - space around the sank ship
     **/

    private final Integer[] sizeOfShips = {4,3,3,2,2,2,1,1,1,1};
    private List<GameModel> gameModelList;
    private Map<UUID, List<GameModelUI>> gameModelUIsForSaving;
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleShipService.class);
    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
    * This method creates a new GameModel and adds it to the gameModelList,
    * or the method finds the existing GameModel, changes it and updates the gameModel list.
    * Then the method converts the GameModel to GameModelUI and returns it
    **/
    public GameModelUI getGameModelUI(PreparingModel preparingModel, boolean isSinglePlayer) {
        LOGGER.debug("getGameModelUI("+preparingModel+", "+isSinglePlayer+")");
        if(preparingModel == null) {
            LOGGER.debug("PreparingModel is null");
            throw new GameModelException("PreparingModel is null");
        }
        if(preparingModel.getPlayerName() == null || preparingModel.getPlayerName().isBlank()) {
            LOGGER.debug("PreparingModel.playerName is null or empty");
            throw new GameModelException("Player's name in PreparingModel can not be null or empty. ");
        }
        PlayerModel enemyModel;
        PlayerModel playerModel = getRandomArrangedShips();
        playerModel.setPlayerName(preparingModel.getPlayerName());
        if(preparingModel.getPlayerId() == null) { // Create new GameModel
            playerModel.setPlayerId(UUID.randomUUID());
            if(isSinglePlayer) {
                enemyModel = getRandomArrangedShips();
                enemyModel.setPlayerId(UUID.randomUUID());
                enemyModel.setPlayerName("Bot");
            } else {
                enemyModel =
                        new PlayerModel(null, "Unknown player", new ArrayList<>(), new int[100]);
            }
            GameModel gameModel = new GameModel(UUID.randomUUID(), playerModel, enemyModel, new ArrayList<>());
            gameModelList.add(gameModel);
            return mapToGameModelUI(gameModel, gameModel.getPlayerModel().getPlayerId());
        } else { // Update existing GameModel
            GameModel gameModel = gameModelList.stream()
                    .filter(e -> e.getPlayerModel().getPlayerId().equals(preparingModel.getPlayerId()))
                    .findFirst().orElse(null);
            if(gameModel != null) {
                playerModel.setPlayerId(preparingModel.getPlayerId());
                gameModel.setPlayerModel(playerModel);
                return mapToGameModelUI(gameModel, gameModel.getPlayerModel().getPlayerId());
            } else {
                LOGGER.debug("PlayerModel with ID "+ preparingModel.getPlayerId() +" not found");
                throw new GameModelException("PlayerModel with ID "+ preparingModel.getPlayerId() +" not found");
            }
        }
    }

    /**
     * This method makes a shot from the bot.
     * 1. The method finds the GameModel and retrieves the PlayerModel
     * 2. The method checks the list of botLastHits and determines a new point for the shot (random or near the hit)
     * 3. The method checks - the bot hit, drowned or missed, updates the battleField, the list of ships and the botLastHits
     * 4. Then the method converts the GameModel to GameModelUI and returns it with the truth activePlayer
     **/
    public GameModelUI botMakesShot(UUID gameId) {
        LOGGER.debug("botMakesShot("+gameId+")");
        GameModel gameModel = getGameModelById(gameId);
        PlayerModel playerModel = gameModel.getPlayerModel();
        UUID activePlayer = gameModel.getEnemyModel().getPlayerId();
        if(!gameModel.getBotLastHits().isEmpty()) {
            int nextHit = getNextHit(gameModel.getBotLastHits(), playerModel.getBattleField());
            if(playerModel.getBattleField()[nextHit] == 1) {
                markBattleFieldIfHit(gameModel, playerModel, nextHit);
            } else {
                playerModel.getBattleField()[nextHit] = 3;
                activePlayer = gameModel.getPlayerModel().getPlayerId();
            }
            gameModel.setPlayerModel(playerModel);
        } else {
            int randomHit = getRandomPoint(playerModel.getBattleField());
            if(playerModel.getBattleField()[randomHit] == 1) {
                markBattleFieldIfHit(gameModel, playerModel, randomHit);
            } else {
                playerModel.getBattleField()[randomHit] = 3;
                activePlayer = gameModel.getPlayerModel().getPlayerId();
            }
            gameModel.setPlayerModel(playerModel);
        }
        saveGameModelUI(gameModel, activePlayer);
        return mapToGameModelUI(gameModel, activePlayer);
    }

    /**
     * This method changes the GameModel, when the Player makes a shot in the SinglePlayerGame
     * 1. The method finds the GameModel and retrieves the EnemyModel
     * 2. The method checks - the Player hit, drowned or missed, updates the battleField, the list of ships
     * 3. Then the method converts the GameModel to GameModelUI and returns it with the truth activePlayer
     **/
    public GameModelUI playerMakesShotForSinglePlayerGame(UUID gameId, int shot) {
        LOGGER.debug("playerMakesShotForSinglePlayerGame("+gameId+", "+shot+")");
        if(shot < 0 || shot > 99) {
            LOGGER.debug("The shot can not be less than '0' and more than '99'");
            throw new GameModelException("The shot can not be less than '0' and more than '99'");
        }
        GameModel gameModel = getGameModelById(gameId);
        PlayerModel enemyModel = gameModel.getEnemyModel();
        UUID activePlayer = gameModel.getPlayerModel().getPlayerId();
        if(enemyModel.getBattleField()[shot] == 1) {
            enemyModel.getBattleField()[shot] = 4;
            if(isSank(shot, enemyModel.getShips(), enemyModel.getBattleField())) {
                markSankShip(shot, enemyModel.getShips(), enemyModel.getBattleField());
            }
        } else {
            enemyModel.getBattleField()[shot] = 3;
            activePlayer = gameModel.getEnemyModel().getPlayerId();
        }
        saveGameModelUI(gameModel, activePlayer);
        return mapToGameModelUI(gameModel, activePlayer);
    }

    /**
     * When the another Player selects the game from the list of Multiplayer games, this method adds him to this GameModel
     * 1. The method finds the GameModel that this Player selected and the GameModel that this player created
     * 2. The method extracts the PlayerModel from the created GameModel and set it to the selected GameModel
     * 3. The method deletes the created GameModel from the gameModelList
     * 4. Then the method converts the GameModel to GameModelUI and returns it
     **/
    public GameModelUI joinToMultiplayerGame(UUID gameId, GameModelUI gameModelUI) {
        LOGGER.debug("joinToMultiplayerGame("+gameId+", "+gameModelUI+")");
        GameModel selectedGameModel = getGameModelById(gameId);
        GameModel createdGameModel = gameModelList.stream()
                .filter(e -> e.getGameId().equals(gameModelUI.getGameId()))
                .findFirst().orElse(null);
        if(createdGameModel != null) {
            selectedGameModel.setEnemyModel(createdGameModel.getPlayerModel());
            deleteGameModelById(createdGameModel.getGameId());
            return mapToGameModelUIForMultiplayerGame(
                    selectedGameModel, gameModelUI.getPlayerModel().getPlayerId());
        } else {
            throw new GameModelException("GameModel with ID "+gameId+" or/and GameModel with ID "
                    +gameModelUI.getGameId()+" is null");
        }
    }

    /**
     * This method changes the GameModel, when the Player makes a shot in the MultiplayerGame
     * 1. The method finds the GameModel and retrieves the truth PlayerModel
     * 2. 2. The method checks - the Player hit, drowned or missed, updates the battleField, the list of ships
     * 3. Then the method converts the GameModel to GameModelUI and returns it with the truth activePlayer
     **/
    public List<GameModelUI> playerMakesShotForMultiplayerGame(UUID gameId, UUID playerId, int shot) {
        LOGGER.debug("playerMakesShotForMultiplayerGame("+gameId+", "+playerId+", "+shot+")");
        if(shot < 0 || shot > 99) {
            LOGGER.debug("The shot can not be less than '0' and more than '99'");
            throw new GameModelException("The shot can not be less than '0' and more than '99'");
        }
        GameModel gameModel = getGameModelById(gameId);
        if(gameModel.getPlayerModel() != null && gameModel.getEnemyModel() != null) {
            PlayerModel updatedPlayerModel = gameModel.getPlayerModel();
            UUID activePlayer = playerId;
            if(gameModel.getPlayerModel().getPlayerId().equals(playerId)) {
                updatedPlayerModel = gameModel.getEnemyModel();
            }
            if(updatedPlayerModel.getBattleField()[shot] == 1) {
                updatedPlayerModel.getBattleField()[shot] = 4;
                if(isSank(shot, updatedPlayerModel.getShips(), updatedPlayerModel.getBattleField())) {
                    markSankShip(shot, updatedPlayerModel.getShips(), updatedPlayerModel.getBattleField());
                }
            } else {
                updatedPlayerModel.getBattleField()[shot] = 3;
                activePlayer = updatedPlayerModel.getPlayerId();
            }
            saveGameModelUI(gameModel, activePlayer);
            GameModelUI gameModelUI1 = mapToGameModelUI(
                    new GameModel(gameId, gameModel.getPlayerModel(), gameModel.getEnemyModel(), new ArrayList<>()),
                    activePlayer);
            GameModelUI gameModelUI2 = mapToGameModelUI(
                    new GameModel(gameId, gameModel.getEnemyModel(), gameModel.getPlayerModel(), new ArrayList<>()),
                    activePlayer);
            return List.of(gameModelUI1, gameModelUI2);
        } else {
            throw new GameModelException("Something went wrong when the Player with ID "+playerId
                    +" tried to make the next hit. Most likely GameModel.PlayerModel or/and GameModel.EnemyModel is null");
        }
    }

    /**
     * The method finds free GameModels for the Multiplayer game from the gameModelList.
     * The GameModel is free, when the EnemyPlayer has fields 'playerId' = null and 'playerName' = 'Unknown player'
     **/
    public List<FreeGame> getFreeGames(UUID playerId) {
        LOGGER.debug("getFreeGames("+playerId+")");
        List<FreeGame> freeGamesList = new ArrayList<>();
        for(GameModel gm : gameModelList) {
            if(gm.getEnemyModel().getPlayerId() == null
                    && !gm.getPlayerModel().getPlayerId().equals(playerId)) {
                freeGamesList.add(new FreeGame(gm.getGameId(), gm.getPlayerModel().getPlayerName()));
            }
        }
        return freeGamesList;
    }

    /**
     * This method deletes the GameModel by its ID from the gameModelList
     **/
    public void deleteGameModelById(UUID gameModelId) {
        LOGGER.debug("deleteGameModelById("+gameModelId+")");
        gameModelList.stream()
                .filter(e -> e.getGameId().equals(gameModelId))
                .findFirst().ifPresent(gameModel -> gameModelList.remove(gameModel));
    }

    /**
     * This method finds the GameModel by its ID from the gameModelList
     **/
    public GameModel getGameModelById(UUID gameId) {
        LOGGER.debug("getGameModelById("+gameId+")");
        GameModel gameModel = gameModelList.stream()
                .filter(e -> e.getGameId().equals(gameId))
                .findFirst().orElse(null);
        if(gameModel != null) {
            return gameModel;
        } else {
            LOGGER.debug("GameModel with ID "+ gameId +" not found");
            throw new GameModelException("GameModel with ID "+ gameId +" not found");
        }
    }

    /**
    * This method converts a GameModel to a GameModelUI
    **/
    public GameModelUI mapToGameModelUIForMultiplayerGame(GameModel gameModel, UUID playerId) {
        LOGGER.debug("mapToGameModelUIForMultiplayerGame("+gameModel+", "+playerId+")");
        PlayerModel playerModel;
        PlayerModel enemyModel;
        UUID activePlayer = playerId;
        if(gameModel.getPlayerModel().getPlayerId().equals(playerId)) {
            playerModel = gameModel.getPlayerModel();
            enemyModel = gameModel.getEnemyModel();
        } else {
            playerModel = gameModel.getEnemyModel();
            enemyModel = gameModel.getPlayerModel();
            activePlayer = gameModel.getPlayerModel().getPlayerId();
        }
        return mapToGameModelUI(
                new GameModel(gameModel.getGameId(), playerModel, enemyModel, new ArrayList<>()), activePlayer
        );
    }

    /**
     * This method gets gameModelUIs from List for saving gameModelUIs, creates new SavingGame model
     * and sends them to service for Kafka
     **/
    public void saveGame(UUID gameModelId) {
        GameModel gameModel = getGameModelById(gameModelId);
        List<GameModelUI> gameModelUIListForSaving = gameModelUIsForSaving.get(gameModelId);
        kafkaProducerService.sendToKafkaGameModelUIs(gameModelUIListForSaving);
        String annotation = gameModel.getPlayerModel().getPlayerName() + " vs "
                + gameModel.getEnemyModel().getPlayerName();
        deleteGameModelById(gameModelId);
        kafkaProducerService.sendToKafkaSavingGame(new SavingGame(annotation, gameModelId));
    }

    /////////////////////////////////////////////////////////////////////////////

    /**
     * This method creates the list of ships and places them around the battleField
     **/
    private PlayerModel getRandomArrangedShips() {
        LOGGER.debug("getRandomArrangedShips()");
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
        return new PlayerModel(null, null, ships, battleField);
    }

    /**
     * This method checks, is it possible to add new ship to the existing battleField
     **/
    private boolean checkNewShip(int point, int shipSize, boolean orientation, int[] battleField) {
        LOGGER.debug("checkNewShip("+point+", "+shipSize+", "+orientation+", "+ Arrays.toString(battleField) +")");
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
        return true;
    }

    /**
     * This method creates a new Ship
     **/
    private Ship createNewShip(int point, int shipSize, boolean orientation) {
        LOGGER.debug("createNewShip("+point+", "+shipSize+", "+orientation+")");
        Set<Integer> coordinates = new TreeSet<>();
        int step = orientation ? 10 : 1;
        for(int i = 0; i < shipSize; i++) {
            coordinates.add(point);
            point = point + step;
        }
        return new Ship(coordinates, createSpaceAroundShip(coordinates));
    }

    /**
     * This method creates the space around a new Ship
     **/
    private Set<Integer> createSpaceAroundShip(Set<Integer> coordinates) {
        LOGGER.debug("createSpaceAroundShip("+coordinates+")");
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
        return space;
    }

    /**
     * Method marks a new ship on the battleField:
     * the ship with the number '1' and the space around this ship with the number '2'
     **/
    private void addNewShipToBattleField(Ship ship, int[] battleField) {
        LOGGER.debug("addNewShipToBattleField("+ship+", "+ Arrays.toString(battleField) +")");
        for(Integer point : ship.getCoordinates()) {
            battleField[point] = 1;
        }
        for(Integer point : ship.getSpaceAround()) {
            battleField[point] = 2;
        }
    }

    /**
     * This method converts a GameModel to a GameModelUI
     * and adds this GameModelUI to List for saving gameModelUIs
     **/
    private void saveGameModelUI(GameModel gameModel, UUID activePlayer) {
        int[] playerBattleField = new int[100];
        int[] enemyBattleField = new int[100];
        for(int i = 0; i < 100; i++) {
            if(gameModel.getPlayerModel().getBattleField()[i] > 2) {
                playerBattleField[i] = gameModel.getPlayerModel().getBattleField()[i];
            }
            if(gameModel.getEnemyModel().getBattleField()[i] > 2) {
                enemyBattleField[i] = gameModel.getEnemyModel().getBattleField()[i];
            }
        }
        PlayerModelUI playerModelUI = new PlayerModelUI(
                gameModel.getPlayerModel().getPlayerId(),
                gameModel.getPlayerModel().getPlayerName(),
                gameModel.getPlayerModel().getShips().size(),
                playerBattleField
        );
        PlayerModelUI enemyModelUI = new PlayerModelUI(
                gameModel.getEnemyModel().getPlayerId(),
                gameModel.getEnemyModel().getPlayerName(),
                gameModel.getEnemyModel().getShips().size(),
                enemyBattleField
        );
        GameModelUI gameModelUI = new GameModelUI(gameModel.getGameId(), playerModelUI, enemyModelUI, activePlayer);
        if(gameModelUIsForSaving.containsKey(gameModel.getGameId())) {
            gameModelUIsForSaving.get(gameModel.getGameId()).add(gameModelUI);
        } else {
            List<GameModelUI> newGameForSaving = new ArrayList<>();
            newGameForSaving.add(gameModelUI);
            gameModelUIsForSaving.put(gameModel.getGameId(), newGameForSaving);
        }
    }

    /**
     * This method converts a GameModel to a GameModelUI
     **/
    private GameModelUI mapToGameModelUI(GameModel gameModel, UUID activePlayer) {
        LOGGER.debug("mapToGameModelUI("+gameModel+", "+activePlayer+")");
        int[] enemyBattleField = new int[100];
        for(int i = 0; i < 100; i++) {
            if(gameModel.getEnemyModel().getBattleField()[i] > 2) {
                enemyBattleField[i] = gameModel.getEnemyModel().getBattleField()[i];
            }
        }
        PlayerModelUI playerModel = mapToPlayerModelUI(gameModel.getPlayerModel());
        PlayerModelUI enemyModel = mapToPlayerModelUI(gameModel.getEnemyModel());
        enemyModel.setBattleField(enemyBattleField);
        return new GameModelUI(gameModel.getGameId(), playerModel, enemyModel, activePlayer);
    }

    /**
     * This method converts a PlayerModel to a PlayerModelUI
     **/
    private PlayerModelUI mapToPlayerModelUI(PlayerModel model) {
        LOGGER.debug("mapToPlayerModelUI("+model+")");
        return new PlayerModelUI(
                model.getPlayerId(), model.getPlayerName(), model.getShips().size(), model.getBattleField());
    }

    /**
     * This method returns the next shot, when the bot hit
     **/
    private int getNextHit(List<Integer> botLastHits, int[] battleField) {
        LOGGER.debug("getNextHit("+botLastHits+", "+ Arrays.toString(battleField) +")");
        if(botLastHits.size() == 1) {
            int hit = botLastHits.get(0);
            if((hit+1) < 100 && battleField[hit+1] < 3 && (hit+1)/10 == hit/10) {return (hit+1);}
            if((hit+10) < 100 && battleField[hit+10] < 3) {return (hit+10);}
            if((hit-1) >= 0 && battleField[hit-1] < 3 && (hit-1)/10 == hit/10) {return (hit-1);}
            if((hit-10) >= 0 && battleField[hit-10] < 3) {return (hit-10);}
        } else if(botLastHits.size() > 1) {
            int firstHit = botLastHits.get(0);
            int secondHit = botLastHits.get(1);
            int lastHit = botLastHits.get(botLastHits.size()-1);
            if(Math.abs(firstHit-secondHit) == 1) {
                if((lastHit-1) >= 0 && (lastHit-1)/10 == lastHit/10 && battleField[lastHit-1] < 3) {return (lastHit-1);}
                if((firstHit+1) < 100 && (firstHit+1)/10 == firstHit/10 && battleField[firstHit+1] < 3) {return (firstHit+1);}
                if((lastHit+1) < 100 && (lastHit+1)/10 == lastHit/10 && battleField[lastHit+1] < 3) {return (lastHit+1);}
                if((firstHit-1) >= 0 && (firstHit-1)/10 == firstHit/10 && battleField[firstHit-1] < 3) {return (firstHit-1);}
            }
            if(Math.abs(firstHit-secondHit) == 10) {
                if ((lastHit - 10) >= 0 && battleField[lastHit - 10] < 3) {return (lastHit-10);}
                if ((firstHit + 10) < 100 && battleField[firstHit + 10] < 3) {return (firstHit+10);}
                if ((lastHit + 10) < 100 && battleField[lastHit + 10] < 3) {return (lastHit+10);}
                if ((firstHit - 10) >= 0 && battleField[firstHit - 10] < 3) {return (firstHit-10);}
            }
        }
        LOGGER.debug("Something went wrong when the Bot tried to make the next hit");
        throw new GameModelException("Something went wrong when the Bot tried to make the next hit");
    }

    /**
     * This method returns a random shot for the bot
     **/
    private int getRandomPoint(int[] battleField) {
        LOGGER.debug("getRandomPoint("+ Arrays.toString(battleField) +")");
        Random random = new Random();
        int randomPoint = random.nextInt(100);
        while(battleField[randomPoint] > 2) {
            randomPoint = random.nextInt(100);
        }
        return randomPoint;
    }

    /**
     * This method marks the battleField, when the bot hit
     **/
    private void markBattleFieldIfHit(GameModel gameModel, PlayerModel playerModel, int hit) {
        LOGGER.debug("markBattleFieldIfHit("+gameModel+", "+playerModel+", "+hit+")");
        playerModel.getBattleField()[hit] = 4;
        if(isSank(hit, playerModel.getShips(), playerModel.getBattleField())) {
            markSankShip(hit, playerModel.getShips(), playerModel.getBattleField());
            gameModel.setBotLastHits(new ArrayList<>());
        } else {
            gameModel.getBotLastHits().add(hit);
        }
    }

    /**
     * This method checks is the ship sank
     **/
    private boolean isSank(int point, List<Ship> ships, int[] battleField) {
        LOGGER.debug("isSank("+point+", "+ships+", "+ Arrays.toString(battleField) +")");
        Ship ship = ships.stream()
                .filter(s -> s.getCoordinates().contains(point))
                .findFirst().orElse(null);
        if(ship != null) {
            for(int coordinate : ship.getCoordinates()) {
                if(battleField[coordinate] != 4) { return false; }
            }
        }
        return true;
    }

    /**
     * This method marks the battleField when the bot drowned a ship
     **/
    private void markSankShip(int hit, List<Ship> ships, int[] battleField) {
        LOGGER.debug("markSankShip("+hit+", "+ships+", "+ Arrays.toString(battleField) +")");
        Ship ship = ships.stream()
                .filter(s -> s.getCoordinates().contains(hit))
                .findFirst().orElse(null);
        if(ship != null) {
            for(int coordinate : ship.getCoordinates()) {
                battleField[coordinate] = 5;
            }
            for(int space : ship.getSpaceAround()) {
                battleField[space] = 6;
            }
            ships.remove(ship);
        }
    }
}