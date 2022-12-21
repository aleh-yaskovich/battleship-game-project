package com.yaskovich.battleship.entity.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputMessage {
    private UUID playerId;
    private String playerName;
    private String text;
}
