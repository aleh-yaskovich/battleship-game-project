package com.yaskovich.battleship.api;

import com.yaskovich.battleship.api.response.BaseResponse;
import com.yaskovich.battleship.api.response.GameModelUIResponse;
import com.yaskovich.battleship.models.PreparingModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RequestMapping("single_player/")
@Tag(
        name = "Single Player controller",
        description = "Controller for the game with a bot"
)
public interface SinglePlayerControllerApi {

    @Operation(
            summary = "Create or update a GameModelUI",
            description = "Create a new GameModelUI or update an existing GameModelUI with random arranged ships"
    )
    @PostMapping("random_battlefield")
    GameModelUIResponse getGameModelUI(@RequestBody PreparingModel preparingModel);

    @Operation(
            summary = "Delete a GameModel",
            description = "Delete a GameModel by its ID from the List of GameModels"
    )
    @GetMapping("game/{gameModelId}/delete")
    BaseResponse deleteGameModel(@PathVariable UUID gameModelId);

    @Operation(
            summary = "Save the game",
            description = "Save the completed game on the Kafka server"
    )
    @GetMapping("game/{gameModelId}/save")
    BaseResponse saveGame(@PathVariable UUID gameModelId);
}
