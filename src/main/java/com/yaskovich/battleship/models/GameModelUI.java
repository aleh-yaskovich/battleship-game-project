package com.yaskovich.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameModelUI {
    private UUID gameId;
    private PlayerModelUI playerModel;
    private PlayerModelUI enemyModel;
    private UUID activePlayer;
}
