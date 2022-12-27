package com.yaskovich.battleship.controllers.unit;

import com.yaskovich.battleship.api.controllers.SinglePlayerController;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PlayerModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import com.yaskovich.battleship.services.BattleShipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SinglePlayerControllerTest {

    @InjectMocks
    private SinglePlayerController controller;

    @Mock
    private BattleShipService service;

//    @Test
//    void shouldReturnGameModelUI() {
//        GameModelUI expected =
//                new GameModelUI(UUID.randomUUID(), new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID());
//        PreparingModel preparingModel = new PreparingModel();
//        when(service.getGameModelUI(preparingModel, true)).thenReturn(expected);
//        GameModelUI actual = controller.getGameModelUI(preparingModel).getBody();
//        verify(service).getGameModelUI(preparingModel, true);
//        assertNotNull(actual);
//        assertEquals(expected, actual);
//    }

//    @Test
//    void shouldDeleteGameModel() {
//        UUID gameModelId = UUID.randomUUID();
//        boolean res = Boolean.TRUE.equals(controller.deleteGameModel(gameModelId).getBody());
//        verify(service).deleteGameModelById(gameModelId);
//        assertTrue(res);
//    }
}
