package com.yaskovich.battleship.services.integration;

import com.yaskovich.battleship.entity.kafka.SavingGame;
import com.yaskovich.battleship.models.GameModelUI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafkaProducerServiceIT {

    @Value("${spring.kafka.topic.games}")
    private String games;
    @Value("${spring.kafka.topic.game-models}")
    private String gameModels;
    @Autowired
    private KafkaTemplate<String, SavingGame> kafkaTemplateForGames;
    @Autowired
    private KafkaTemplate<String, GameModelUI> kafkaTemplateForGameModels;

    @Test
    void sendToKafkaSavingGameTest() throws Exception {
        SavingGame data = new SavingGame("A vs B", UUID.randomUUID());
        ListenableFuture<SendResult<String, SavingGame>> future =
                kafkaTemplateForGames.send(games, data);
        assertNotNull(future);
        SendResult<String, SavingGame> result = future.get();
        assertEquals(data, result.getProducerRecord().value());
    }

    @Test
    void sendToKafkaGameModelUIsTest() throws Exception {
        GameModelUI gameModelUI = new GameModelUI();
        gameModelUI.setGameId(UUID.randomUUID());
        ListenableFuture<SendResult<String, GameModelUI>> future =
                kafkaTemplateForGameModels.send(gameModels, gameModelUI);
        assertNotNull(future);
        SendResult<String, GameModelUI> result = future.get();
        assertEquals(gameModelUI, result.getProducerRecord().value());
    }
}
