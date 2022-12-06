package com.yaskovich.battleship.api;

import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<GameModelUI> getGameModelUI(@RequestBody PreparingModel preparingModel);

    @Operation(
            summary = "Delete a GameModel",
            description = "Delete a GameModel by its ID from the List of GameModels"
    )
    @DeleteMapping("game/{gameModelId}")
    ResponseEntity<Boolean> deleteGameModel(@PathVariable UUID gameModelId);
}
