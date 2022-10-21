package com.yaskovich.battleship.models;

import com.yaskovich.battleship.entity.Ship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BattleFieldModel {
    private List<Ship> ships;
    private int[] battleField;
}
