package com.yaskovich.battleship.controllers.unit;

import com.yaskovich.battleship.api.controllers.MultiplayerController;
import com.yaskovich.battleship.api.response.BaseResponse;
import com.yaskovich.battleship.api.response.GameModelUIResponse;
import com.yaskovich.battleship.models.FreeGame;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PlayerModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import com.yaskovich.battleship.services.BattleShipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MultiplayerControllerTest {

    @InjectMocks
    private MultiplayerController controller;

    @Mock
    private BattleShipService service;

    @Test
    void getGameModelUITest() {
        GameModelUI expected =
                new GameModelUI(UUID.randomUUID(), new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID());
        PreparingModel preparingModel = new PreparingModel();
        when(service.getGameModelUI(preparingModel, false)).thenReturn(expected);
        GameModelUIResponse actual = controller.getGameModelUI(preparingModel);
        verify(service).getGameModelUI(preparingModel, false);
        assertNotNull(actual);
        assertNotNull(actual.getGameModelUI());
        assertEquals(BaseResponse.Status.SUCCESS, actual.getStatus());
        assertEquals(expected, actual.getGameModelUI());
    }

    @Test
    void getFreeGamesTest() {
        List<FreeGame> expected = List.of(new FreeGame());
        UUID playerId = UUID.randomUUID();
        when(service.getFreeGames(playerId)).thenReturn(expected);
        List<FreeGame> actual = controller.getFreeGames(playerId);
        verify(service).getFreeGames(playerId);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void joinToMultiplayerGameTest() {
        UUID gameId = UUID.randomUUID();
        GameModelUI expected =
                new GameModelUI(UUID.randomUUID(), new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID());
        when(service.joinToMultiplayerGame(gameId, new GameModelUI())).thenReturn(expected);
        GameModelUIResponse actual = controller.joinToMultiplayerGame(gameId, new GameModelUI());
        verify(service).joinToMultiplayerGame(gameId, new GameModelUI());
        assertNotNull(actual);
        assertNotNull(actual.getGameModelUI());
        assertEquals(expected, actual.getGameModelUI());
    }
}
