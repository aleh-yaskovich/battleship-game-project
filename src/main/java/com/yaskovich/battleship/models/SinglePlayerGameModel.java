package com.yaskovich.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SinglePlayerGameModel {
    private BattleFieldModel playerModel;
    private BattleFieldModel botModel;
}
