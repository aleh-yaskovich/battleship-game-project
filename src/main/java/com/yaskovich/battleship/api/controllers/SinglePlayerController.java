package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.api.SinglePlayerControllerApi;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.SinglePlayerGameModel;
import com.yaskovich.battleship.services.SinglePlayerService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class SinglePlayerController implements SinglePlayerControllerApi {

    @Autowired
    private SinglePlayerService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePlayerController.class);

    @Override
    public ResponseEntity<BattleFieldModel> createRandomBattleField() {
        LOGGER.debug("The method createRandomBattleField() worked");
        BattleFieldModel model = service.getRandomArrangedShips();
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SinglePlayerGameModel> makeHit(Integer point, SinglePlayerGameModel model) {
        SinglePlayerGameModel updatedModel = service.makeHit(point, model);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }
}
