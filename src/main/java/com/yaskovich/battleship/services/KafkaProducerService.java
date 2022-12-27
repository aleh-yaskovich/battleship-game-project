package com.yaskovich.battleship.services;

import com.yaskovich.battleship.entity.kafka.SavingGame;
import com.yaskovich.battleship.exceptions.KafkaProducerException;
import com.yaskovich.battleship.models.GameModelUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);
    @Value("${spring.kafka.topic.games}")
    private String games;
    @Value("${spring.kafka.topic.game-models}")
    private String gameModels;
    @Autowired
    private KafkaTemplate<String, SavingGame> kafkaTemplateForGames;
    @Autowired
    private KafkaTemplate<String, GameModelUI> kafkaTemplateForGameModels;

    /**
     * This method sends to Kafka server SavingGame with information about players names and gameId
     **/
    public void sendToKafkaSavingGame(SavingGame game) {
        ListenableFuture<SendResult<String, SavingGame>> future =
                kafkaTemplateForGames.send(games, game);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, SavingGame> result) {
                LOGGER.debug("Sent message=[" + game +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.debug("Unable to send message=["
                        + game + "] due to : " + ex.getMessage());
                throw new KafkaProducerException("Failed to send to Kafka server savingGame: " + game.toString());
            }
        });
    }

    /**
     * This method sends to Kafka server a list of models related to the same game
     **/
    public void sendToKafkaGameModelUIs (List<GameModelUI>gameModelUIList) {
        for(GameModelUI gameModelUI : gameModelUIList) {
            ListenableFuture<SendResult<String, GameModelUI>> future =
                    kafkaTemplateForGameModels.send(gameModels, gameModelUI);
            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, GameModelUI> result) {
                    LOGGER.debug("Sent message=[" + gameModelUI +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                }
                @Override
                public void onFailure(Throwable e) {
                    LOGGER.debug("Unable to send message=["
                            + gameModelUI + "] due to : " + e.getMessage());
                    throw new KafkaProducerException(
                            "Failed to send to Kafka server gameModelUI: " + gameModelUI.toString()
                    );
                }
            });
        }
    }
}
