package com.yaskovich.battleship.models;

import com.yaskovich.battleship.entity.Ship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlayerModel {
    private UUID playerId;
    private String playerName;
    private List<Ship> ships;
    private int[] battleField;
}
