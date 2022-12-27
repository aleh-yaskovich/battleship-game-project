package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.api.MultiplayerControllerApi;
import com.yaskovich.battleship.api.response.BaseResponse;
import com.yaskovich.battleship.api.response.GameModelUIResponse;
import com.yaskovich.battleship.exceptions.GameModelException;
import com.yaskovich.battleship.models.FreeGame;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import com.yaskovich.battleship.services.BattleShipService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public GameModelUIResponse getGameModelUI(PreparingModel preparingModel) {
        LOGGER.debug("getGameModelUI("+preparingModel+") started");
        try {
            GameModelUI gameModelUI = service.getGameModelUI(preparingModel, false);
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
     * Get the List of games in which another player is not defined
     **/
    @Override
    public List<FreeGame> getFreeGames(UUID withoutId) {
        LOGGER.debug("getFreeGames("+withoutId+") started");
        return service.getFreeGames(withoutId);
    }

    /**
     * A player joins to the selected free game and the method returns the updated GameModelUI to both players
     **/
    @Override
    public GameModelUIResponse joinToMultiplayerGame(UUID gameId, GameModelUI gameModelUI) {
        LOGGER.debug("joinToMultiplayerGame("+gameId+", "+gameModelUI+") started");
        try {
            GameModelUI joinedGameModelUI = service.joinToMultiplayerGame(gameId, gameModelUI);
            return GameModelUIResponse.builder()
                    .gameModelUI(joinedGameModelUI)
                    .status(BaseResponse.Status.SUCCESS)
                    .build();
        } catch (GameModelException e) {
            LOGGER.debug("joinToMultiplayerGame: " + e.getMessage());
            return GameModelUIResponse.builder()
                    .message(e.getMessage())
                    .status(BaseResponse.Status.FAILURE)
                    .build();
        }
    }
}
