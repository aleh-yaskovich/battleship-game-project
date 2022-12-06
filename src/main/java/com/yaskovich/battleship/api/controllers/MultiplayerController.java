package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.api.MultiplayerControllerApi;
import com.yaskovich.battleship.models.FreeGame;
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

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@RestController
public class MultiplayerController implements MultiplayerControllerApi {

    @Autowired
    private BattleShipService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplayerController.class);

    /**
     * Create a new GameModelUI or update an existing GameModelUI with random arranged ships
    **/
    @Override
    public ResponseEntity<GameModelUI> getGameModelUI(PreparingModel preparingModel) {
        GameModelUI gameModelUI = service.getGameModelUI(preparingModel, false);
        return new ResponseEntity<>(gameModelUI, HttpStatus.OK);
    }

    /**
     * Get the List of games in which another player is not defined
     **/
    @Override
    public ResponseEntity<List<FreeGame>> getFreeGames(UUID withoutId) {
        List<FreeGame> freeGames = service.getFreeGames(withoutId);
        return new ResponseEntity<>(freeGames, HttpStatus.OK);
    }

    /**
     * A player joins to the selected free game and the method returns the updated GameModelUI to both players
     **/
    @Override
    public ResponseEntity<GameModelUI> joinToMultiplayerGame(UUID gameId, GameModelUI gameModelUI) {
        GameModelUI joinedGameModelUI = service.joinToMultiplayerGame(gameId, gameModelUI);
        return new ResponseEntity<>(joinedGameModelUI, HttpStatus.OK);
    }
}
