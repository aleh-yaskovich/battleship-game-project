package com.yaskovich.battleship.controllers.unit;

import com.yaskovich.battleship.api.controllers.SinglePlayerController;
import com.yaskovich.battleship.entity.Ship;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.SinglePlayerGameModel;
import com.yaskovich.battleship.services.SinglePlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SinglePlayerControllerTest {

    @InjectMocks
    private SinglePlayerController controller;

    @Mock
    private SinglePlayerService service;

    @Test
    void shouldReturnBattleFieldModel() {
        List<Ship> ships = List.of(new Ship(), new Ship());
        int[] battleField = new int[100];
        BattleFieldModel expected = new BattleFieldModel(ships, battleField);

        when(service.getRandomArrangedShips()).thenReturn(expected);

        ResponseEntity<BattleFieldModel> actual = controller.createRandomBattleField();
        assertNotNull(actual);
        assertNotNull(actual.getBody());

        BattleFieldModel actualBattleFieldModel = actual.getBody();
        assertEquals(expected, actualBattleFieldModel);
    }

    @Test
    void shouldReturnSinglePlayerGameModel() {
        SinglePlayerGameModel model = new SinglePlayerGameModel();
        model.setBotLastHits(new ArrayList<>());
        model.setBotStatus(true);
        Random random = new Random();

        SinglePlayerGameModel expected = new SinglePlayerGameModel();
        expected.setBotLastHits(List.of(1,2,3));
        expected.setBotStatus(true);

        when(service.makeHit(anyInt(), any(SinglePlayerGameModel.class))).thenReturn(expected);

        ResponseEntity<SinglePlayerGameModel> actual =
                controller.makeHit(random.nextInt(100), model);
        assertNotNull(actual);
        assertNotNull(actual.getBody());

        SinglePlayerGameModel actualSinglePlayerGameModel = actual.getBody();
        assertEquals(expected, actualSinglePlayerGameModel);
    }
}
