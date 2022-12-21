package com.yaskovich.battleship.controllers.unit;

import com.yaskovich.battleship.api.controllers.WebSocketController;
import com.yaskovich.battleship.entity.messages.InputMessage;
import com.yaskovich.battleship.entity.messages.OutputMessage;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PlayerModel;
import com.yaskovich.battleship.models.PlayerModelUI;
import com.yaskovich.battleship.services.BattleShipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class WebSocketControllerTest {

    @InjectMocks
    private WebSocketController controller;
    @Mock
    private BattleShipService service;
    @Mock
    private SimpMessagingTemplate template;

    @Test
    void shouldReturnGameModelUIWhenPlayerMakesShot() {
        UUID gameId = UUID.randomUUID();
        int shot = 0;
        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());
        when(service.playerMakesShotForSinglePlayerGame(gameId, shot)).thenReturn(expected);
        GameModelUI actual = controller.getGameModelWhenPlayerMakesShot(gameId, shot);
        verify(service).playerMakesShotForSinglePlayerGame(gameId, shot);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnGameModelUIWhenBotMakesShot() {
        UUID gameId = UUID.randomUUID();
        GameModelUI expected = new GameModelUI();
        expected.setGameId(UUID.randomUUID());
        when(service.botMakesShot(gameId)).thenReturn(expected);
        GameModelUI actual = controller.getGameModelWhenBotMakesShot(gameId);
        verify(service).botMakesShot(gameId);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateGameModelUI() {
        UUID gameId = UUID.randomUUID();
        PlayerModel playerModel = new PlayerModel(UUID.randomUUID(), "Name", new ArrayList<>(), new int[100]);
        GameModel gameModel = new GameModel(gameId, playerModel, playerModel, new ArrayList<>());
        UUID playerId = playerModel.getPlayerId();
        GameModelUI gameModelUI = new GameModelUI(gameId, new PlayerModelUI(), new PlayerModelUI(), playerId);

        when(service.getGameModelById(gameId)).thenReturn(gameModel);
        when(service.mapToGameModelUIForMultiplayerGame(gameModel, playerId)).thenReturn(gameModelUI);
        controller.updateGameModelUI(gameId);
        verify(service).getGameModelById(gameId);
        verify(service).mapToGameModelUIForMultiplayerGame(gameModel, playerId);
        verify(template).convertAndSend("/topic/game/"+gameId+"/player/"+playerId, gameModelUI);
    }

    @Test
    void shouldMakeShotAndReturnGameModelUI() {
        PlayerModelUI playerModelUI = new PlayerModelUI();
        playerModelUI.setPlayerId(UUID.randomUUID());
        PlayerModelUI enemyModelUI = new PlayerModelUI();
        enemyModelUI.setPlayerId(UUID.randomUUID());

        UUID gameId = UUID.randomUUID();
        UUID playerId = playerModelUI.getPlayerId();
        int shot = 0;

        GameModelUI gameModelUI1 = new GameModelUI(gameId, playerModelUI, enemyModelUI, playerId);
        GameModelUI gameModelUI2 = new GameModelUI(gameId, enemyModelUI, playerModelUI, playerId);
        List<GameModelUI> gameModelUIList = List.of(gameModelUI1, gameModelUI2);

        when(service.playerMakesShotForMultiplayerGame(gameId, playerId, shot)).thenReturn(gameModelUIList);
        controller.makeShotAndReturnGameModelUI(gameId, playerId, shot);
        verify(service).playerMakesShotForMultiplayerGame(gameId, playerId, shot);
        verify(template).convertAndSend(
                "/topic/game/"+gameId+"/player/"+gameModelUI1.getPlayerModel().getPlayerId()+"/update",
                gameModelUI1);
        verify(template).convertAndSend(
                "/topic/game/"+gameId+"/player/"+gameModelUI2.getPlayerModel().getPlayerId()+"/update",
                gameModelUI2);
    }

    @Test
    void shouldSendMessage() {
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
