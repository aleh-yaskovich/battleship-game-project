package com.yaskovich.battleship.services;

import com.yaskovich.battleship.exceptions.GameModelException;
import com.yaskovich.battleship.models.FreeGame;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BattleShipServiceIT {

    @Autowired
    private BattleShipService service;

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
        service.deleteGameModelById(actual.getGameId());
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
        service.deleteGameModelById(actualGameModelUI.getGameId());
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
        service.deleteGameModelById(actual.getGameId());
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
        service.deleteGameModelById(actualGameModelUI.getGameId());
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
    void botMakesShotTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        service.botMakesShot(gameModel.getGameId());
        int count = 0;
        GameModel actual = service.getGameModelById(gameModel.getGameId());
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameModel.getGameId(), actual.getGameId());
        assertNotNull(actual.getPlayerModel());
        assertNotNull(actual.getPlayerModel().getBattleField());
        for(int point : actual.getPlayerModel().getBattleField()) {
            if(point > 2) count++;
        }
        assertEquals(1, count);
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void botMakesShotAndHitsTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        gameModel.getBotLastHits().add(1);
        gameModel.getPlayerModel().getBattleField()[1] = 4;
        service.getGameModelList().add(gameModel);
        service.botMakesShot(gameModel.getGameId());
        GameModel actual = service.getGameModelById(gameModel.getGameId());
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameModel.getGameId(), actual.getGameId());
        assertNotNull(actual.getPlayerModel());
        assertNotNull(actual.getPlayerModel().getBattleField());
        assertEquals(4, actual.getPlayerModel().getBattleField()[1]);
        assertEquals(4, actual.getPlayerModel().getBattleField()[2]);
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void botMakesShotAndSinksShipTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        int[] ship = {0,1,2,3};
        int[] spaceAround = {4,10,11,12,13,14};
        gameModel.getBotLastHits().add(0);
        gameModel.getBotLastHits().add(1);
        gameModel.getBotLastHits().add(2);
        gameModel.getPlayerModel().getBattleField()[0] = 4;
        gameModel.getPlayerModel().getBattleField()[1] = 4;
        gameModel.getPlayerModel().getBattleField()[2] = 4;
        service.getGameModelList().add(gameModel);
        service.botMakesShot(gameModel.getGameId());
        GameModel actual = service.getGameModelById(gameModel.getGameId());
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameModel.getGameId(), actual.getGameId());
        assertNotNull(actual.getPlayerModel());
        assertNotNull(actual.getPlayerModel().getBattleField());
        for(int sh : ship) {
            assertEquals(5, actual.getPlayerModel().getBattleField()[sh]);
        }
        for(int sp : spaceAround) {
            assertEquals(6, actual.getPlayerModel().getBattleField()[sp]);
        }
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void playerMakesShotInSinglePlayerGamaTest() {
        int shot = 99;
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        GameModelUI actual = service.playerMakesShotForSinglePlayerGame(gameModel.getGameId(), shot);
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameModel.getGameId(), actual.getGameId());
        assertNotNull(actual.getEnemyModel());
        assertNotNull(actual.getEnemyModel().getBattleField());
        assertEquals(3, actual.getEnemyModel().getBattleField()[shot]);
        assertEquals(gameModel.getEnemyModel().getPlayerId(), actual.getActivePlayer());
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void playerMakesShotAndHitsInSinglePlayerGamaTest() {
        int shot = 0;
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        GameModelUI actual = service.playerMakesShotForSinglePlayerGame(gameModel.getGameId(), shot);
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameModel.getGameId(), actual.getGameId());
        assertNotNull(actual.getEnemyModel());
        assertNotNull(actual.getEnemyModel().getBattleField());
        assertEquals(4, actual.getEnemyModel().getBattleField()[shot]);
        assertEquals(gameModel.getPlayerModel().getPlayerId(), actual.getActivePlayer());
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void playerMakesShotAndSinksShipInSinglePlayerGamaTest() {
        int shot = 3;
        GameModel gameModel = GameModelObjectMother.getGameModel();
        gameModel.getEnemyModel().getBattleField()[0] = 4;
        gameModel.getEnemyModel().getBattleField()[1] = 4;
        gameModel.getEnemyModel().getBattleField()[2] = 4;
        service.getGameModelList().add(gameModel);
        GameModelUI actual = service.playerMakesShotForSinglePlayerGame(gameModel.getGameId(), shot);
        assertNotNull(actual);
        assertNotNull(actual.getGameId());
        assertEquals(gameModel.getGameId(), actual.getGameId());
        assertNotNull(actual.getEnemyModel());
        assertNotNull(actual.getEnemyModel().getBattleField());
        assertEquals(5, actual.getEnemyModel().getBattleField()[shot]);
        assertEquals(6, actual.getEnemyModel().getBattleField()[shot+1]);
        assertEquals(0, actual.getEnemyModel().getSizeOfShips());
        assertEquals(gameModel.getPlayerModel().getPlayerId(), actual.getActivePlayer());
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void playerMakesShotInMultiplayerGamaTest() {
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
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void playerMakesShotAndHitsInMultiplayerGamaTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);
        UUID gameId = gameModel.getGameId();
        UUID playerId = gameModel.getPlayerModel().getPlayerId();
        int shot = 0;
        List<GameModelUI> gameModelUIList = service.playerMakesShotForMultiplayerGame(gameId, playerId, shot);

        GameModelUI gm1 = gameModelUIList.get(0);
        assertNotNull(gm1.getGameId());
        assertEquals(gameId, gm1.getGameId());
        assertEquals(gameModel.getPlayerModel().getPlayerId(), gm1.getPlayerModel().getPlayerId());
        assertEquals(gm1.getPlayerModel().getPlayerId(), gm1.getActivePlayer());
        assertEquals(4, gm1.getEnemyModel().getBattleField()[shot]);

        GameModelUI gm2 = gameModelUIList.get(1);
        assertNotNull(gm2.getGameId());
        assertEquals(gameId, gm2.getGameId());
        assertEquals(gameModel.getEnemyModel().getPlayerId(), gm2.getPlayerModel().getPlayerId());
        assertEquals(gm2.getEnemyModel().getPlayerId(), gm2.getActivePlayer());
        assertEquals(4, gm2.getPlayerModel().getBattleField()[shot]);
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void playerMakesShotAndSinksShipInMultiplayerGamaTest() {
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
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void getFreeGamesForMultiplayerGameTest() {
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
        service.deleteGameModelById(gameModelUI.getGameId());
    }

    @Test
    void joinToMultiplayerGameTest() {
        GameModelUI firstGameModelUI =
                service.getGameModelUI(new PreparingModel(null, "Player1"), false);
        GameModelUI secondGameModelUI =
                service.getGameModelUI( new PreparingModel(null, "Player2"), false);
        GameModelUI actual = service.joinToMultiplayerGame(firstGameModelUI.getGameId(), secondGameModelUI);
        assertNotNull(actual);
        assertEquals(firstGameModelUI.getGameId(), actual.getGameId());
        assertEquals(firstGameModelUI.getPlayerModel().getPlayerId(), actual.getEnemyModel().getPlayerId());
        assertEquals(secondGameModelUI.getPlayerModel().getPlayerId(), actual.getPlayerModel().getPlayerId());
        assertEquals(actual.getEnemyModel().getPlayerId(), actual.getActivePlayer());
        assertThrows(RuntimeException.class, () -> service.getGameModelById(secondGameModelUI.getGameId()));
        service.deleteGameModelById(firstGameModelUI.getGameId());
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
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void getGameModelByIdWithWrongIdTest() {
        UUID randomGameId = UUID.randomUUID();
        String message = "GameModel with ID "+ randomGameId +" not found";
        assertThrows(RuntimeException.class, () -> service.getGameModelById(randomGameId), message);
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
}