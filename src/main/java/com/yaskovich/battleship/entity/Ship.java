package com.yaskovich.battleship.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ship {

    private Set<Integer> coordinates;
    private Set<Integer> spaceAround;
    private boolean status;
}
