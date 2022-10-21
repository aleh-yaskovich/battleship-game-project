package com.yaskovich.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiplayerGameModel {
    private BattleFieldModel playerModel;
    private BattleFieldModel botModel;
}
