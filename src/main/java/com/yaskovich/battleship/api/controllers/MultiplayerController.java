package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.api.MultiplayerControllerApi;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.MultiplayerGameModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MultiplayerController implements MultiplayerControllerApi {

    @Override
    public BattleFieldModel createRandomBattleField() {
        return null;
    }

    @Override
    public List<String> getWaitingList() {
        return null;
    }

    @Override
    public ResponseEntity<MultiplayerGameModel> makeHit(Integer point, MultiplayerGameModel model) {
        return null;
    }

    @Override
    public ResponseEntity<String> sendMessage(Integer playerId, String message) {
        return null;
    }
}
