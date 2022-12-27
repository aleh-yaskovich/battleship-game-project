package com.yaskovich.battleship.controllers.unit;

import com.yaskovich.battleship.api.controllers.MultiplayerController;
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

//    @Test
//    void shouldReturnGameModelUI() {
//        GameModelUI expected =
//                new GameModelUI(UUID.randomUUID(), new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID());
//        PreparingModel preparingModel = new PreparingModel();
//        when(service.getGameModelUI(preparingModel, false)).thenReturn(expected);
//        GameModelUI actual = controller.getGameModelUI(preparingModel).getBody();
//        verify(service).getGameModelUI(preparingModel, false);
//        assertNotNull(actual);
//        assertEquals(expected, actual);
//    }

//    @Test
//    void shouldReturnFreeGameList() {
//        List<FreeGame> expected = List.of(new FreeGame());
//        UUID playerId = UUID.randomUUID();
//        when(service.getFreeGames(playerId)).thenReturn(expected);
//        List<FreeGame> actual = controller.getFreeGames(playerId).getBody();
//        verify(service).getFreeGames(playerId);
//        assertNotNull(actual);
//        assertEquals(expected, actual);
//    }

//    @Test
//    void shouldJoinToMultiplayerGame() {
//        UUID gameId = UUID.randomUUID();
//        GameModelUI expected =
//                new GameModelUI(UUID.randomUUID(), new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID());
//        when(service.joinToMultiplayerGame(gameId, new GameModelUI())).thenReturn(expected);
//        GameModelUI actual = controller.joinToMultiplayerGame(gameId, new GameModelUI()).getBody();
//        verify(service).joinToMultiplayerGame(gameId, new GameModelUI());
//        assertNotNull(actual);
//        assertEquals(expected, actual);
//    }
}
