package com.yaskovich.battleship.api;

import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.MultiplayerGameModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("multiplayer/")
@Tag(
        name = "Multiplayer controller",
        description = "Controller for the game with another player"
)
public interface MultiplayerControllerApi {

    @Operation(
            summary = "Creating a battle field with random arranged ships",
            description = "Creating a battle field with random arranged ships"
    )
    @GetMapping("/preparing/random_battlefield")
    public BattleFieldModel createRandomBattleField();

    @Operation(
            summary = "Return a waiting list",
            description = "Return a list with players who are waiting for an opponent"
    )
    @GetMapping("/preparing/waiting_list")
    public List<String> getWaitingList();

    @Operation(
            summary = "Send a selected point and return an updated battle field",
            description = "Send a selected point and return an updated battle field"
    )
    @PostMapping("game/{point}")
    public ResponseEntity<MultiplayerGameModel> makeHit(
            @PathVariable Integer point, @RequestBody MultiplayerGameModel model);

    @Operation(
            summary = "Send a message",
            description = "Send a message for the opponent"
    )
    @PostMapping("game/{playerId}/message")
    public ResponseEntity<String> sendMessage(
            @PathVariable Integer playerId, @RequestBody String message);
}
