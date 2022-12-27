package com.yaskovich.battleship.api.controllers;

import com.yaskovich.battleship.entity.messages.InputMessage;
import com.yaskovich.battleship.entity.messages.OutputMessage;
import com.yaskovich.battleship.models.GameModel;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.services.BattleShipService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    /**
     * This method is used when the player makes shot in the single player game.
     * The method returns the GameModelUI with changes after the shot
     **/
    @MessageMapping("/game/{gameId}/{shot}")
    @SendTo("/topic/game")
    public GameModelUI getGameModelWhenPlayerMakesShot(
            @DestinationVariable("gameId") UUID gameId,
            @DestinationVariable("shot") int shot) {
        LOGGER.debug("getGameModelWhenPlayerMakesShot("+gameId+", "+shot+") started");
        return service.playerMakesShotForSinglePlayerGame(gameId, shot);
    }

    /**
     * This method is used when the bot makes shot in the single player game.
     * The method returns the GameModelUI with changes after the shot
     **/
    @MessageMapping("/game/{gameId}/bot")
    @SendTo("/topic/game")
    public GameModelUI getGameModelWhenBotMakesShot(
            @DestinationVariable("gameId") UUID gameId) {
        LOGGER.debug("getGameModelWhenBotMakesShot("+gameId+") started");
        return service.botMakesShot(gameId);
    }

    /**
     * When the second player joins the game, information about him is added to the model,
     * and the modified model is returned to the first player
     **/
    @MessageMapping("/game/{gameId}")
    public void updateGameModelUI(@DestinationVariable("gameId") UUID gameId) {
        LOGGER.debug("updateGameModelUI("+gameId+") started");
        GameModel gameModel = service.getGameModelById(gameId);
        UUID playerId = gameModel.getPlayerModel().getPlayerId();
        GameModelUI gameModelUI = service.mapToGameModelUIForMultiplayerGame(gameModel, playerId);
        template.convertAndSend("/topic/game/"+gameId+"/player/"+playerId, gameModelUI);
    }

    /**
     * This method is used when the player makes shot in the multiplayer game.
     * The method returns two GameModelUIs with changes after the shot to both players
     **/
    @MessageMapping("/game/{gameId}/player/{playerId}/shot/{shot}")
    public void makeShotAndReturnGameModelUI(
            @DestinationVariable("gameId") UUID gameId,
            @DestinationVariable("playerId") UUID playerId,
            @DestinationVariable("shot") int shot
    ) {
        LOGGER.debug("makeShotAndReturnGameModelUI("+gameId+", "+playerId+", "+shot+") started");
        List<GameModelUI> gameModelUIList =
                service.playerMakesShotForMultiplayerGame(gameId, playerId, shot);
        gameModelUIList.forEach(e -> {
            String url = "/topic/game/"+gameId+"/player/"+e.getPlayerModel().getPlayerId()+"/update";
            template.convertAndSend(url, e);});
    }

    /**
     * This method is used when one of the players in the multiplayer game leaves the game early.
     * The method creates a GameModelUI that notifies the other player about the early end of the game
     **/
    @MessageMapping("game/{gameId}/player/{playerId}/quit")
    public void interruptGame(
            @DestinationVariable("gameId") UUID gameId,
            @DestinationVariable("playerId") UUID playerId
    ) {
        LOGGER.debug("interruptGame("+gameId+", "+playerId+") started");
        GameModel gameModel = service.getGameModelById(gameId);
        UUID enemyId = gameModel.getPlayerModel().getPlayerId();
        if(gameModel.getPlayerModel().getPlayerId().equals(playerId)) {
            enemyId = gameModel.getEnemyModel().getPlayerId();
        }
        GameModelUI gameModelUI = service.interruptGame(gameId, playerId);
        template.convertAndSend("/topic/game/"+gameId+"/player/"+enemyId+"/update", gameModelUI);
        service.deleteGameModelById(gameId);
    }

    /**
     * This method is used when players send and received messages during the multiplayer game
     **/
    @MessageMapping("/game/{gameId}/messenger")
    public OutputMessage sendMessage(
            @DestinationVariable("gameId") String gameId, InputMessage inputMessage) {
        LOGGER.debug("sendMessage("+gameId+", "+inputMessage+") started");
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(inputMessage.getPlayerId(), inputMessage.getPlayerName(), inputMessage.getText(), time);
    }
}
