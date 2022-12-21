package com.yaskovich.battleship.services;

import com.yaskovich.battleship.entity.kafka.SavingGame;
import com.yaskovich.battleship.models.GameModel;
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

    public boolean sendToKafkaSavingGame(SavingGame game) {
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
            }
        });
        return true;
    }

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
                public void onFailure(Throwable ex) {
                    LOGGER.debug("Unable to send message=["
                            + gameModelUI + "] due to : " + ex.getMessage());
                }
            });
        }
    }
}
