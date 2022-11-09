package com.yaskovich.battleship.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessage {

    private String from;
    private String text;
    private String time;
}
