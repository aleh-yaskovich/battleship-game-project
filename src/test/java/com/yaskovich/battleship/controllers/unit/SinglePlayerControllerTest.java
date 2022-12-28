package com.yaskovich.battleship.controllers.unit;

import com.yaskovich.battleship.api.controllers.SinglePlayerController;
import com.yaskovich.battleship.api.response.BaseResponse;
import com.yaskovich.battleship.api.response.GameModelUIResponse;
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

    @Test
    void getGameModelUITest() {
        GameModelUI expected =
                new GameModelUI(UUID.randomUUID(), new PlayerModelUI(), new PlayerModelUI(), UUID.randomUUID());
        PreparingModel preparingModel = new PreparingModel();
        when(service.getGameModelUI(preparingModel, true)).thenReturn(expected);
        GameModelUIResponse actual = controller.getGameModelUI(preparingModel);
        verify(service).getGameModelUI(preparingModel, true);
        assertNotNull(actual);
        assertNotNull(actual.getGameModelUI());
        assertEquals(BaseResponse.Status.SUCCESS, actual.getStatus());
        assertEquals(expected, actual.getGameModelUI());
    }

    @Test
    void deleteGameModelTest() {
        UUID gameModelId = UUID.randomUUID();
        BaseResponse actual = controller.deleteGameModel(gameModelId);
        verify(service).deleteGameModelById(gameModelId);
        assertNotNull(actual);
        assertEquals(BaseResponse.Status.FAILURE, actual.getStatus());
    }

    @Test
    void saveGameTest() {
        UUID gameModelId = UUID.randomUUID();
        BaseResponse actual = controller.saveGame(gameModelId);
        verify(service).saveGame(gameModelId);
        assertNotNull(actual);
        assertEquals(BaseResponse.Status.SUCCESS, actual.getStatus());
    }
}
