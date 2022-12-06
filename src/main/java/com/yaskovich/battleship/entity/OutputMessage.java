package com.yaskovich.battleship.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputMessage {
    private UUID playerId;
    private String playerName;
    private String text;
    private String time;
}
