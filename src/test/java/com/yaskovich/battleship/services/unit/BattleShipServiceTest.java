package com.yaskovich.battleship.services.unit;

import com.yaskovich.battleship.entity.kafka.SavingGame;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.services.BattleShipService;
import com.yaskovich.battleship.services.GameModelObjectMother;
import com.yaskovich.battleship.services.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class BattleShipServiceTest {

    @InjectMocks
    private BattleShipService service;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Test
    void saveGameTest() {
        GameModel gameModel = GameModelObjectMother.getGameModel();
        UUID gameId = gameModel.getGameId();
        UUID playerId = gameModel.getPlayerModel().getPlayerId();

        service.setGameModelList(new ArrayList<>());
        service.getGameModelList().add(gameModel);

        GameModelUI gameModelUI = service.mapToGameModelUIForMultiplayerGame(gameModel, playerId);
        Map<UUID, List<GameModelUI>> gameModelUIsForSaving = new HashMap<>();
        List<GameModelUI> gameModelUIs = new ArrayList<>();
        gameModelUIs.add(gameModelUI);
        gameModelUIsForSaving.put(gameId, gameModelUIs);
        service.setGameModelUIsForSaving(gameModelUIsForSaving);

        assertEquals(1, service.getGameModelList().size());
        service.saveGame(gameModel.getGameId());
        assertEquals(0, service.getGameModelList().size());
        assertEquals(0, service.getGameModelUIsForSaving().size());

        String annotation = gameModel.getPlayerModel().getPlayerName() + " vs "
                + gameModel.getEnemyModel().getPlayerName();
        SavingGame expected = new SavingGame(annotation, gameId);

        verify(kafkaProducerService).sendToKafkaGameModelUIs(gameModelUIs);
        verify(kafkaProducerService).sendToKafkaSavingGame(expected);
    }
}
