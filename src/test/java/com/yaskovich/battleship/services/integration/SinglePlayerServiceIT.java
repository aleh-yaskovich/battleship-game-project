package com.yaskovich.battleship.services.integration;

import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.services.SinglePlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SinglePlayerServiceIT {

    @Autowired
    private SinglePlayerService service;

    @Test
    void shouldReturnBattleFieldModel() {
        BattleFieldModel actual = service.getRandomArrangedShips();
        assertNotNull(actual);
        assertNotNull(actual.getBattleField());
        assertNotNull(actual.getShips());
        assertEquals(actual.getBattleField().length, 100);
        assertEquals(actual.getShips().size(), 10);
    }
}
