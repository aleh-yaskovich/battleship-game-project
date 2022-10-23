package com.yaskovich.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SinglePlayerGameModel {
    private BattleFieldModel battleFieldModel;
    private List<Integer> botLastHits;
    private boolean botStatus;
}
