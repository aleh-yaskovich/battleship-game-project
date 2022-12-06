package com.yaskovich.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameModel {
    private UUID gameId;
    private PlayerModel playerModel;
    private PlayerModel enemyModel;
    private List<Integer> botLastHits;
}
