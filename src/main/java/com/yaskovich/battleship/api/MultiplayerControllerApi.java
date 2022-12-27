package com.yaskovich.battleship.api;

import com.yaskovich.battleship.api.response.GameModelUIResponse;
import com.yaskovich.battleship.models.FreeGame;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RequestMapping("multiplayer/")
@Tag(
        name = "Multiplayer controller",
        description = "Controller for the game with an another player"
)
public interface MultiplayerControllerApi {

    @Operation(
            summary = "Create or update a GameModelUI",
            description = "Create a new GameModelUI or update an existing GameModelUI with random arranged ships"
    )
    @PostMapping("random_battlefield")
    GameModelUIResponse getGameModelUI(@RequestBody PreparingModel preparingModel);

    @Operation(
            summary = "Get the List of free games",
            description = "Get the List of games in which another player is not defined"
    )
    @GetMapping("free_games")
    List<FreeGame> getFreeGames(@RequestParam UUID withoutId);

    @Operation(
            summary = "Join an another player to the selected free game",
            description = "Join an another player to the selected free game " +
                    "and return the updated GameModelUI to both players"
    )
    @PostMapping("game/{gameId}/join")
    GameModelUIResponse joinToMultiplayerGame(
            @PathVariable UUID gameId, @RequestBody GameModelUI gameModelUI);
}
