package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.api.SinglePlayerControllerApi;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import com.yaskovich.battleship.services.BattleShipService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GameModelUI> getGameModelUI(PreparingModel preparingModel) {
        GameModelUI gameModelUI = service.getGameModelUI(preparingModel, true);
        return new ResponseEntity<>(gameModelUI, HttpStatus.OK);
    }

    /**
     * Delete a GameModel by its ID from the List of GameModels
     **/
    @Override
    public ResponseEntity<Boolean> deleteGameModel(UUID gameModelId) {
        service.deleteGameModelById(gameModelId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Boolean> saveGame(UUID gameModelId) {
        service.saveGame(gameModelId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
