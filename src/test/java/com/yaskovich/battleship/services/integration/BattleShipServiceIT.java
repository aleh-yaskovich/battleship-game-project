package com.yaskovich.battleship.services.integration;

import com.yaskovich.battleship.exceptions.GameModelException;
import com.yaskovich.battleship.models.FreeGame;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import com.yaskovich.battleship.services.BattleShipService;
import com.yaskovich.battleship.services.GameModelObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BattleShipServiceIT {

    @Autowired
    private BattleShipService service;

    @Test
    void botMakesShotTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        UUID gameId = gameModel.getGameId();

        GameModelUI actual = service.botMakesShot(gameId);
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameId, actual.getGameId());
        assertNotNull(actual.getPlayerModel());
        assertEquals(gameModel.getPlayerModel().getPlayerId(), actual.getPlayerModel().getPlayerId());
        assertEquals(gameModel.getPlayerModel().getPlayerName(), actual.getPlayerModel().getPlayerName());
        assertEquals(gameModel.getPlayerModel().getShips().size(), actual.getPlayerModel().getSizeOfShips());
        assertNotNull(actual.getEnemyModel());
        assertEquals(gameModel.getEnemyModel().getPlayerId(), actual.getEnemyModel().getPlayerId());
        assertEquals(gameModel.getEnemyModel().getPlayerName(), actual.getEnemyModel().getPlayerName());
        assertEquals(gameModel.getEnemyModel().getShips().size(), actual.getEnemyModel().getSizeOfShips());
        assertNotNull(actual.getPlayerModel().getBattleField());
        int count = 0;
        for(int i : actual.getPlayerModel().getBattleField()) {
            if(i > 2) { count++; }
        }
        assertEquals(1, count);
        List<GameModelUI> gameModelUIListForSaving = service.getGameModelUIsForSaving().get(gameId);
        assertNotNull(gameModelUIListForSaving);
        assertEquals(1, gameModelUIListForSaving.size());
        service.setGameModelUIsForSaving(new HashMap<>());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void botMakesNextShotTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        gameModel.getPlayerModel().getBattleField()[0] = 4;
        gameModel.getPlayerModel().getBattleField()[1] = 4;
        gameModel.getBotLastHits().add(1);
        gameModel.getBotLastHits().add(0);
        service.getGameModelList().add(gameModel);
        assertEquals(1, service.getGameModelList().size());
        UUID gameId = gameModel.getGameId();

        GameModelUI actual = service.botMakesShot(gameId);
        assertEquals(4, actual.getPlayerModel().getBattleField()[2]);
        GameModel gameModelActual = service.getGameModelList().get(0);
        assertEquals(3, gameModelActual.getBotLastHits().size());
        assertEquals(2, gameModelActual.getBotLastHits().get(2));
        service.setGameModelUIsForSaving(new HashMap<>());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void botMakesShotAndSinksShipTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        assertEquals(1, gameModel.getPlayerModel().getShips().size());
        gameModel.getPlayerModel().getBattleField()[0] = 4;
        gameModel.getPlayerModel().getBattleField()[1] = 4;
        gameModel.getPlayerModel().getBattleField()[2] = 4;
        gameModel.getBotLastHits().add(1);
        gameModel.getBotLastHits().add(0);
        gameModel.getBotLastHits().add(2);
        service.getGameModelList().add(gameModel);
        assertEquals(1, service.getGameModelList().size());
        UUID gameId = gameModel.getGameId();

        GameModelUI actual = service.botMakesShot(gameId);
        assertEquals(0, actual.getPlayerModel().getSizeOfShips());
        assertEquals(5, actual.getPlayerModel().getBattleField()[0]);
        assertEquals(5, actual.getPlayerModel().getBattleField()[3]);
        assertEquals(6, actual.getPlayerModel().getBattleField()[4]);
        assertEquals(6, actual.getPlayerModel().getBattleField()[10]);
        GameModel gameModelActual = service.getGameModelList().get(0);
        assertEquals(0, gameModelActual.getBotLastHits().size());
        service.setGameModelUIsForSaving(new HashMap<>());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void playerMakesShotForSinglePlayerGameTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        UUID gameId = gameModel.getGameId();
        int shot = 0;

        GameModelUI actual = service.playerMakesShotForSinglePlayerGame(gameId, shot);
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameId, actual.getGameId());
        assertNotNull(actual.getPlayerModel());
        assertEquals(gameModel.getPlayerModel().getPlayerId(), actual.getPlayerModel().getPlayerId());
        assertEquals(gameModel.getPlayerModel().getPlayerName(), actual.getPlayerModel().getPlayerName());
        assertEquals(gameModel.getPlayerModel().getShips().size(), actual.getPlayerModel().getSizeOfShips());
        assertNotNull(actual.getEnemyModel());
        assertEquals(gameModel.getEnemyModel().getPlayerId(), actual.getEnemyModel().getPlayerId());
        assertEquals(gameModel.getEnemyModel().getPlayerName(), actual.getEnemyModel().getPlayerName());
        assertEquals(gameModel.getEnemyModel().getShips().size(), actual.getEnemyModel().getSizeOfShips());
        assertNotNull(actual.getEnemyModel().getBattleField());
        assertEquals(4, actual.getEnemyModel().getBattleField()[shot]);
        List<GameModelUI> gameModelUIListForSaving = service.getGameModelUIsForSaving().get(gameId);
        assertNotNull(gameModelUIListForSaving);
        assertEquals(1, gameModelUIListForSaving.size());
        service.setGameModelUIsForSaving(new HashMap<>());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void playerMakesShotForSinglePlayerGameAndSinksShipTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        assertEquals(1, gameModel.getEnemyModel().getShips().size());
        gameModel.getEnemyModel().getBattleField()[0] = 4;
        gameModel.getEnemyModel().getBattleField()[1] = 4;
        gameModel.getEnemyModel().getBattleField()[2] = 4;
        service.getGameModelList().add(gameModel);
        assertEquals(1, service.getGameModelList().size());
        UUID gameId = gameModel.getGameId();
        int shot = 3;

        GameModelUI actual = service.playerMakesShotForSinglePlayerGame(gameId, shot);
        assertEquals(0, actual.getEnemyModel().getSizeOfShips());
        assertEquals(5, actual.getEnemyModel().getBattleField()[0]);
        assertEquals(5, actual.getEnemyModel().getBattleField()[3]);
        assertEquals(6, actual.getEnemyModel().getBattleField()[4]);
        assertEquals(6, actual.getEnemyModel().getBattleField()[10]);
        service.setGameModelUIsForSaving(new HashMap<>());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void playerMakesShotForMultiplayerGameTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        UUID gameId = gameModel.getGameId();
        UUID playerId = gameModel.getPlayerModel().getPlayerId();
        int shot = 99;
        List<GameModelUI> gameModelUIList = service.playerMakesShotForMultiplayerGame(gameId, playerId, shot);
        assertNotNull(gameModelUIList);
        assertEquals(2, gameModelUIList.size());

        GameModelUI gm1 = gameModelUIList.get(0);
        assertNotNull(gm1.getGameId());
        assertEquals(gameId, gm1.getGameId());
        assertEquals(gameModel.getPlayerModel().getPlayerId(), gm1.getPlayerModel().getPlayerId());
        assertEquals(gameModel.getPlayerModel().getBattleField(), gm1.getPlayerModel().getBattleField());
        assertEquals(gm1.getEnemyModel().getPlayerId(), gm1.getActivePlayer());
        assertEquals(3, gm1.getEnemyModel().getBattleField()[shot]);

        GameModelUI gm2 = gameModelUIList.get(1);
        assertNotNull(gm2.getGameId());
        assertEquals(gameId, gm2.getGameId());
        assertEquals(gameModel.getEnemyModel().getPlayerId(), gm2.getPlayerModel().getPlayerId());
        assertEquals(gameModel.getEnemyModel().getBattleField(), gm2.getPlayerModel().getBattleField());
        assertEquals(gm2.getPlayerModel().getPlayerId(), gm2.getActivePlayer());
        assertEquals(3, gm2.getPlayerModel().getBattleField()[shot]);
        List<GameModelUI> gameModelUIListForSaving = service.getGameModelUIsForSaving().get(gameId);
        assertNotNull(gameModelUIListForSaving);
        assertEquals(1, gameModelUIListForSaving.size());
        service.setGameModelUIsForSaving(new HashMap<>());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void playerMakesShotForMultiplayerGameAndSinksShipTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        gameModel.getEnemyModel().getBattleField()[0] = 4;
        gameModel.getEnemyModel().getBattleField()[1] = 4;
        gameModel.getEnemyModel().getBattleField()[2] = 4;
        service.getGameModelList().add(gameModel);
        UUID gameId = gameModel.getGameId();
        UUID playerId = gameModel.getPlayerModel().getPlayerId();
        int shot = 3;
        List<GameModelUI> gameModelUIList = service.playerMakesShotForMultiplayerGame(gameId, playerId, shot);

        GameModelUI gm1 = gameModelUIList.get(0);
        assertNotNull(gm1.getGameId());
        assertEquals(gameId, gm1.getGameId());
        assertEquals(gameModel.getPlayerModel().getPlayerId(), gm1.getPlayerModel().getPlayerId());
        assertEquals(gm1.getPlayerModel().getPlayerId(), gm1.getActivePlayer());
        assertEquals(0, gm1.getEnemyModel().getSizeOfShips());
        assertEquals(5, gm1.getEnemyModel().getBattleField()[shot]);
        assertEquals(6, gm1.getEnemyModel().getBattleField()[shot+1]);

        GameModelUI gm2 = gameModelUIList.get(1);
        assertNotNull(gm2.getGameId());
        assertEquals(gameId, gm2.getGameId());
        assertEquals(gameModel.getEnemyModel().getPlayerId(), gm2.getPlayerModel().getPlayerId());
        assertEquals(gm2.getEnemyModel().getPlayerId(), gm2.getActivePlayer());
        assertEquals(0, gm2.getPlayerModel().getSizeOfShips());
        assertEquals(5, gm2.getPlayerModel().getBattleField()[shot]);
        assertEquals(6, gm2.getPlayerModel().getBattleField()[shot+1]);
        service.setGameModelUIsForSaving(new HashMap<>());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void getFreeGamesTest() {
        PreparingModel preparingModel = new PreparingModel(null, "Name");
        GameModelUI gameModelUI = service.getGameModelUI(preparingModel, false);
        UUID randomPlayerId = UUID.randomUUID();
        UUID existingPlayerId = gameModelUI.getPlayerModel().getPlayerId();

        List<FreeGame> freeGames = service.getFreeGames(randomPlayerId);
        assertNotNull(freeGames);
        assertEquals(1, freeGames.size());
        assertEquals(gameModelUI.getGameId(), freeGames.get(0).getGameId());
        assertEquals(gameModelUI.getPlayerModel().getPlayerName(), freeGames.get(0).getPlayerName());

        freeGames = service.getFreeGames(existingPlayerId);
        assertNotNull(freeGames);
        assertEquals(0, freeGames.size());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void joinToMultiplayerGameTest() {
        GameModelUI firstGameModelUI =
                service.getGameModelUI(new PreparingModel(null, "Player1"), false);
        GameModelUI secondGameModelUI =
                service.getGameModelUI( new PreparingModel(null, "Player2"), false);
        assertEquals(2, service.getGameModelList().size());
        GameModelUI actual = service.joinToMultiplayerGame(firstGameModelUI.getGameId(), secondGameModelUI);
        assertNotNull(actual);
        assertEquals(firstGameModelUI.getGameId(), actual.getGameId());
        assertEquals(firstGameModelUI.getPlayerModel().getPlayerId(), actual.getEnemyModel().getPlayerId());
        assertEquals(secondGameModelUI.getPlayerModel().getPlayerId(), actual.getPlayerModel().getPlayerId());
        assertEquals(actual.getEnemyModel().getPlayerId(), actual.getActivePlayer());
        assertThrows(RuntimeException.class, () -> service.getGameModelById(secondGameModelUI.getGameId()));
        assertEquals(1, service.getGameModelList().size());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void mapToGameModelUIForMultiplayerGameTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        UUID activePlayerId = gameModel.getPlayerModel().getPlayerId();
        GameModelUI actual = service.mapToGameModelUIForMultiplayerGame(gameModel, activePlayerId);
        assertNotNull(actual);
        assertEquals(gameModel.getGameId(), actual.getGameId());
        assertEquals(gameModel.getPlayerModel().getPlayerId(), actual.getPlayerModel().getPlayerId());
        assertEquals(activePlayerId, actual.getActivePlayer());
    }

    @Test
    void interruptGameTest() {
        GameModelUI firstGameModelUI =
                service.getGameModelUI(new PreparingModel(null, "Player1"), false);
        GameModelUI secondGameModelUI =
                service.getGameModelUI( new PreparingModel(null, "Player2"), false);
        assertEquals(2, service.getGameModelList().size());
        GameModelUI joinedGameModelUI = service.joinToMultiplayerGame(firstGameModelUI.getGameId(), secondGameModelUI);
        assertEquals(1, service.getGameModelList().size());
        UUID surrenderedPlayer = joinedGameModelUI.getPlayerModel().getPlayerId();
        GameModelUI actual = service.interruptGame(joinedGameModelUI.getGameId(), surrenderedPlayer);
        assertEquals(surrenderedPlayer, actual.getEnemyModel().getPlayerId());
        assertEquals(-1, actual.getEnemyModel().getSizeOfShips());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void createNewGameModelUIForSinglePlayerGameTest() {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        boolean isSinglePlayer = true;
        GameModelUI actual = service.getGameModelUI(preparingModel, isSinglePlayer);
        assertNotNull(actual);
        assertNotNull(actual.getPlayerModel());
        assertNotNull(actual.getEnemyModel());
        assertEquals(actual.getPlayerModel().getPlayerName(), expectedName);
        assertEquals(actual.getPlayerModel().getSizeOfShips(), 10);
        assertEquals(actual.getEnemyModel().getPlayerName(), "Bot");
        assertEquals(actual.getActivePlayer(), actual.getPlayerModel().getPlayerId());

        assertEquals(1, service.getGameModelList().size());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void updateGameModelUIForSinglePlayerGameTest() {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        boolean isSinglePlayer = true;
        GameModelUI newGameModelUI = service.getGameModelUI(preparingModel, isSinglePlayer);
        assertNotNull(newGameModelUI);
        assertNotNull(newGameModelUI.getPlayerModel());
        assertNotNull(newGameModelUI.getEnemyModel());

        preparingModel.setPlayerId(newGameModelUI.getPlayerModel().getPlayerId());
        GameModelUI actualGameModelUI = service.getGameModelUI(preparingModel, isSinglePlayer);
        assertNotNull(actualGameModelUI);
        assertNotNull(actualGameModelUI.getPlayerModel());
        assertNotNull(actualGameModelUI.getEnemyModel());
        assertEquals(actualGameModelUI.getPlayerModel().getPlayerName(), expectedName);
        assertEquals(actualGameModelUI.getPlayerModel().getSizeOfShips(), 10);
        assertEquals(actualGameModelUI.getEnemyModel().getPlayerName(), "Bot");
        assertEquals(actualGameModelUI.getActivePlayer(), actualGameModelUI.getPlayerModel().getPlayerId());

        assertEquals(newGameModelUI.getGameId(), actualGameModelUI.getGameId());
        assertEquals(newGameModelUI.getEnemyModel(), actualGameModelUI.getEnemyModel());
        assertEquals(newGameModelUI.getPlayerModel().getPlayerId(), actualGameModelUI.getPlayerModel().getPlayerId());
        assertEquals(newGameModelUI.getPlayerModel().getPlayerName(), actualGameModelUI.getPlayerModel().getPlayerName());
        assertNotEquals(newGameModelUI.getPlayerModel().getBattleField(), actualGameModelUI.getPlayerModel().getBattleField());

        assertEquals(1, service.getGameModelList().size());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void createNewGameModelUIForMultiplayerGameTest() {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        boolean isSinglePlayer = false;
        GameModelUI actual = service.getGameModelUI(preparingModel, isSinglePlayer);
        assertNotNull(actual);
        assertNotNull(actual.getPlayerModel());
        assertNotNull(actual.getEnemyModel());
        assertEquals(actual.getPlayerModel().getPlayerName(), expectedName);
        assertEquals(actual.getPlayerModel().getSizeOfShips(), 10);
        assertEquals(actual.getEnemyModel().getPlayerName(), "Unknown player");
        assertNull(actual.getEnemyModel().getPlayerId());
        assertEquals(actual.getActivePlayer(), actual.getPlayerModel().getPlayerId());

        assertEquals(1, service.getGameModelList().size());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void updateGameModelUIForMultiplayerGameTest() {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        boolean isSinglePlayer = false;
        GameModelUI newGameModelUI = service.getGameModelUI(preparingModel, isSinglePlayer);
        assertNotNull(newGameModelUI);
        assertNotNull(newGameModelUI.getPlayerModel());
        assertNotNull(newGameModelUI.getEnemyModel());

        preparingModel.setPlayerId(newGameModelUI.getPlayerModel().getPlayerId());
        GameModelUI actualGameModelUI = service.getGameModelUI(preparingModel, isSinglePlayer);
        assertNotNull(actualGameModelUI);
        assertNotNull(actualGameModelUI.getPlayerModel());
        assertNotNull(actualGameModelUI.getEnemyModel());
        assertEquals(actualGameModelUI.getPlayerModel().getPlayerName(), expectedName);
        assertEquals(actualGameModelUI.getPlayerModel().getSizeOfShips(), 10);
        assertEquals(actualGameModelUI.getEnemyModel().getPlayerName(), "Unknown player");
        assertNull(actualGameModelUI.getEnemyModel().getPlayerId());
        assertEquals(actualGameModelUI.getActivePlayer(), actualGameModelUI.getPlayerModel().getPlayerId());

        assertEquals(newGameModelUI.getGameId(), actualGameModelUI.getGameId());
        assertEquals(newGameModelUI.getEnemyModel(), actualGameModelUI.getEnemyModel());
        assertEquals(newGameModelUI.getPlayerModel().getPlayerId(), actualGameModelUI.getPlayerModel().getPlayerId());
        assertEquals(newGameModelUI.getPlayerModel().getPlayerName(), actualGameModelUI.getPlayerModel().getPlayerName());
        assertNotEquals(newGameModelUI.getPlayerModel().getBattleField(), actualGameModelUI.getPlayerModel().getBattleField());

        assertEquals(1, service.getGameModelList().size());
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void createNewGameModelUIWithWrongPlayerNameTest() {
        PreparingModel preparingModel = new PreparingModel(null, null);
        assertThrows(GameModelException.class, () -> service.getGameModelUI(preparingModel, true));
        preparingModel.setPlayerName("");
        assertThrows(GameModelException.class, () -> service.getGameModelUI(preparingModel, true));
        preparingModel.setPlayerName(" ");
        assertThrows(GameModelException.class, () -> service.getGameModelUI(preparingModel, true));
    }

    @Test
    void deleteGameModelByIdTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        assertEquals(1, service.getGameModelList().size());
        assertEquals(gameModel, service.getGameModelList().get(0));
        service.deleteGameModelById(gameModel.getGameId());
        assertEquals(0, service.getGameModelList().size());
    }

    @Test
    void getGameModelByIdTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        assertEquals(1, service.getGameModelList().size());
        assertEquals(gameModel, service.getGameModelList().get(0));
        GameModel actual = service.getGameModelById(gameModel.getGameId());
        assertNotNull(actual);
        assertEquals(gameModel, actual);
        service.setGameModelList(new ArrayList<>());
    }

    @Test
    void getGameModelByIdWithWrongIdTest() {
        UUID randomGameId = UUID.randomUUID();
        String message = "GameModel with ID "+ randomGameId +" not found";
        assertThrows(RuntimeException.class, () -> service.getGameModelById(randomGameId), message);
    }
}