package com.yaskovich.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerModelUI {
    private UUID playerId;
    private String playerName;
    private Integer sizeOfShips;
    private int[] battleField;
}
