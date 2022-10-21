package com.yaskovich.battleship.controllers.unit;

import com.yaskovich.battleship.api.controllers.SinglePlayerController;
import com.yaskovich.battleship.entity.Ship;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.services.SinglePlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        BattleFieldModel model = new BattleFieldModel(ships, battleField);

        when(service.getRandomArrangedShips()).thenReturn(model);

        ResponseEntity<BattleFieldModel> response = controller.createRandomBattleField();
        assertNotNull(response);
        assertNotNull(response.getBody());

        BattleFieldModel actual = response.getBody();
        assertEquals(model, actual);
    }
}
