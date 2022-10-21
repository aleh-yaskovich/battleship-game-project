package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.api.SinglePlayerControllerApi;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.SinglePlayerGameModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SinglePlayerController implements SinglePlayerControllerApi {

    @Override
    public BattleFieldModel createRandomBattleField() {
        return null;
    }

    @Override
    public ResponseEntity<SinglePlayerGameModel> makeHit(Integer point, SinglePlayerGameModel model) {
        return new ResponseEntity<>(new SinglePlayerGameModel(), HttpStatus.OK);
    }
}
