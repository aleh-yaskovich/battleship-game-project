package com.yaskovich.battleship.api.response;

import com.yaskovich.battleship.models.GameModelUI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GameModelUIResponse extends BaseResponse {
    private GameModelUI gameModelUI;
}
