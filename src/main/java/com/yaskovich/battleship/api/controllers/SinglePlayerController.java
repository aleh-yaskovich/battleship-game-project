package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.api.SinglePlayerControllerApi;
import com.yaskovich.battleship.api.response.BaseResponse;
import com.yaskovich.battleship.api.response.GameModelUIResponse;
import com.yaskovich.battleship.exceptions.GameModelException;
import com.yaskovich.battleship.exceptions.KafkaProducerException;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import com.yaskovich.battleship.services.BattleShipService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Data
@AllArgsConstructor
@RestController
public class SinglePlayerController implements SinglePlayerControllerApi {

    @Autowired
    private BattleShipService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePlayerController.class);

    /**
     * Create a new GameModelUI or update an existing GameModelUI with random arranged ships
     **/
    @Override
    public GameModelUIResponse getGameModelUI(PreparingModel preparingModel) {
        LOGGER.debug("getGameModelUI("+preparingModel+") started");
        try {
            GameModelUI gameModelUI = service.getGameModelUI(preparingModel, true);
            return GameModelUIResponse.builder()
                    .gameModelUI(gameModelUI)
                    .status(BaseResponse.Status.SUCCESS)
                    .build();
        } catch (GameModelException e) {
            LOGGER.debug("getGameModelUI: " + e.getMessage());
            return GameModelUIResponse.builder()
                    .message(e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }

    /**
     * Delete a GameModel by its ID from the List of GameModels
     **/
    @Override
    public BaseResponse deleteGameModel(UUID gameModelId) {
        LOGGER.debug("deleteGameModel("+gameModelId+") started");
        if (service.deleteGameModelById(gameModelId)) {
            return BaseResponse.builder().status(BaseResponse.Status.SUCCESS).build();
        } else {
            return BaseResponse.builder().status(BaseResponse.Status.FAILURE).build();
        }
    }

    /**
     * Save the completed game on the Kafka server
     **/
    @Override
    public BaseResponse saveGame(UUID gameModelId) {
        LOGGER.debug("saveGame("+gameModelId+") started");
        try {
            service.saveGame(gameModelId);
            return BaseResponse.builder().status(BaseResponse.Status.SUCCESS).build();
        } catch (GameModelException | KafkaProducerException e) {
            LOGGER.debug("saveGame: " + e.getMessage());
            return BaseResponse.builder()
                    .message(e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }
}
