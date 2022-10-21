package com.yaskovich.battleship.api;

import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.SinglePlayerGameModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("single_player/")
@Tag(
        name = "Single Player controller",
        description = "Controller for the game with a bot"
)
public interface SinglePlayerControllerApi {

    @Operation(
            summary = "Creating a battle field with random arranged ships",
            description = "Creating a battle field with random arranged ships"
    )
    @GetMapping("/preparing/random_battlefield")
    public ResponseEntity<BattleFieldModel> createRandomBattleField();

    @Operation(
            summary = "Send a selected point and return an updated battle field",
            description = "Send a selected point and return an updated battle field"
    )
    @PostMapping("game/{point}")
    public ResponseEntity<SinglePlayerGameModel> makeHit(
            @PathVariable Integer point, @RequestBody SinglePlayerGameModel model);
}
