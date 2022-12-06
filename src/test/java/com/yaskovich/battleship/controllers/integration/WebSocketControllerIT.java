package com.yaskovich.battleship.controllers.integration;

import com.yaskovich.battleship.api.controllers.WebSocketController;
import com.yaskovich.battleship.entity.InputMessage;
import com.yaskovich.battleship.entity.OutputMessage;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.services.BattleShipService;
import com.yaskovich.battleship.services.GameModelObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class WebSocketControllerIT {

    @Autowired
    private BattleShipService service;
    @Autowired
    private WebSocketController controller;

    @Test
    void botMakesShotTest() {
        GameModel model = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(model);

        UUID gameId = model.getGameId();
        GameModelUI actual = controller.getGameModelWhenBotMakesShot(gameId);
        assertNotNull(actual);
        assertEquals(model.getGameId(), actual.getGameId());
        assertEquals(model.getPlayerModel().getPlayerId(),  actual.getPlayerModel().getPlayerId());
        assertEquals(model.getEnemyModel().getPlayerName(), actual.getEnemyModel().getPlayerName());
        int count = 0;
        for(int point : actual.getPlayerModel().getBattleField()) {
            if(point > 2) count++;
        }
        assertTrue(count > 0);
        service.deleteGameModelById(model.getGameId());
    }

    @Test
    void playerMakesShotInSinglePlayerGamaTest() {
        GameModel model = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(model);

        UUID gameId = model.getGameId();
        int shot = 0;
        GameModelUI actual = controller.getGameModelWhenPlayerMakesShot(gameId, shot);
        assertNotNull(actual);
        assertEquals(model.getGameId(), actual.getGameId());
        assertEquals(model.getPlayerModel().getPlayerId(),  actual.getPlayerModel().getPlayerId());
        assertEquals(model.getEnemyModel().getPlayerName(), actual.getEnemyModel().getPlayerName());
        assertEquals(model.getPlayerModel().getBattleField(), actual.getPlayerModel().getBattleField());
        assertEquals(4, actual.getEnemyModel().getBattleField()[shot]);
        assertEquals(actual.getPlayerModel().getPlayerId(), actual.getActivePlayer());
        service.deleteGameModelById(model.getGameId());
    }

    @Test
    void playerMakesShotInMultiplayerGamaTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        service.getGameModelList().add(gameModel);

        UUID gameId = gameModel.getGameId();
        UUID playerId = gameModel.getEnemyModel().getPlayerId();
        int shot = 0;
        assertEquals(1, gameModel.getPlayerModel().getBattleField()[shot]);

        controller.makeShotAndReturnGameModelUI(gameId, playerId, shot);
        assertEquals(1, service.getGameModelList().size());
        GameModel actual = service.getGameModelList().get(0);
        assertEquals(4, actual.getPlayerModel().getBattleField()[shot]);
        service.deleteGameModelById(gameModel.getGameId());
    }

    @Test
    void sendMessageTest() {
        String gameId = UUID.randomUUID().toString();
        UUID playerId = UUID.randomUUID();
        String playerName = "Name";
        String text = "Message";
        InputMessage inputMessage = new InputMessage(playerId, playerName, text);
        OutputMessage actual = controller.sendMessage(gameId, inputMessage);
        assertNotNull(actual);
        assertEquals(playerId, actual.getPlayerId());
        assertEquals(playerName, actual.getPlayerName());
        assertEquals(text, actual.getText());
        assertTrue(actual.getTime().matches("\\d{2}:\\d{2}"));
    }
}