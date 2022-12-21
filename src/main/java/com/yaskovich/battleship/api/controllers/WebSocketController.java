package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.entity.messages.InputMessage;
import com.yaskovich.battleship.entity.messages.OutputMessage;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.services.BattleShipService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class WebSocketController {

    @Autowired
    private BattleShipService service;
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/game/{gameId}/{shot}")
    @SendTo("/topic/game")
    public GameModelUI getGameModelWhenPlayerMakesShot(
            @DestinationVariable("gameId") UUID gameId,
            @DestinationVariable("shot") int shot) {
        return service.playerMakesShotForSinglePlayerGame(gameId, shot);
    }

    @MessageMapping("/game/{gameId}/bot")
    @SendTo("/topic/game")
    public GameModelUI getGameModelWhenBotMakesShot(
            @DestinationVariable("gameId") UUID gameId) {
        return service.botMakesShot(gameId);
    }

    @MessageMapping("/game/{gameId}")
    public void updateGameModelUI(@DestinationVariable("gameId") UUID gameId) {
        GameModel gameModel = service.getGameModelById(gameId);
        UUID playerId = gameModel.getPlayerModel().getPlayerId();
        GameModelUI gameModelUI = service.mapToGameModelUIForMultiplayerGame(gameModel, playerId);
        template.convertAndSend("/topic/game/"+gameId+"/player/"+playerId, gameModelUI);
    }

    @MessageMapping("/game/{gameId}/player/{playerId}/shot/{shot}")
    public void makeShotAndReturnGameModelUI(
            @DestinationVariable("gameId") UUID gameId,
            @DestinationVariable("playerId") UUID playerId,
            @DestinationVariable("shot") int shot
    ) {
        List<GameModelUI> gameModelUIList =
                service.playerMakesShotForMultiplayerGame(gameId, playerId, shot);
        gameModelUIList.forEach(e -> {
            String url = "/topic/game/"+gameId+"/player/"+e.getPlayerModel().getPlayerId()+"/update";
            template.convertAndSend(url, e);});
    }

    @MessageMapping("/game/{gameId}/messenger")
    public OutputMessage sendMessage(
            @DestinationVariable("gameId") String gameId, InputMessage inputMessage) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(inputMessage.getPlayerId(), inputMessage.getPlayerName(), inputMessage.getText(), time);
    }
}
