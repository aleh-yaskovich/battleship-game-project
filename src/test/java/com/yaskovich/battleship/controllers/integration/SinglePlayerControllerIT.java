package com.yaskovich.battleship.controllers.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaskovich.battleship.api.controllers.SinglePlayerController;
import com.yaskovich.battleship.entity.Ship;
import com.yaskovich.battleship.models.BattleFieldModel;
import com.yaskovich.battleship.models.SinglePlayerGameModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class SinglePlayerControllerIT {

    public static final String SINGLE_PLAYER_ENDPOINT = "/single_player";
    @Autowired
    private SinglePlayerController singlePlayerController;
    @Autowired
    protected ObjectMapper objectMapper;
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = standaloneSetup(singlePlayerController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnRandomArrangedShipsAndBattleFieldTest() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(get(SINGLE_PLAYER_ENDPOINT+"/preparing/random_battlefield")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        BattleFieldModel result = objectMapper.readValue(response.getContentAsString(), BattleFieldModel.class);
        assertNotNull(result);
        assertEquals(result.getShips().size(), 10);
        assertEquals(result.getShips().get(0).getCoordinates().size(), 4);
        assertEquals(result.getShips().get(1).getCoordinates().size(), 3);
        assertEquals(result.getShips().get(3).getCoordinates().size(), 2);
        assertEquals(result.getShips().get(9).getCoordinates().size(), 1);
        assertEquals(result.getBattleField().length, 100);
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenPlayerHits() throws Exception {
        SinglePlayerGameModel model = new SinglePlayerGameModel();
        model.setBattleFieldModel(getBattleFieldModel());
        int point = 8;
        assertEquals(model.getBattleFieldModel().getBattleField()[point], 1);

        String modelJson = objectMapper.writeValueAsString(model);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/game/"+point)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        SinglePlayerGameModel actual =
                objectMapper.readValue(response.getContentAsString(), SinglePlayerGameModel.class);
        assertNotNull(actual);
        assertEquals(actual.getBattleFieldModel().getBattleField()[point], 4);
        assertFalse(actual.isBotStatus());
        assertEquals(model.getBattleFieldModel().getShips(), actual.getBattleFieldModel().getShips());
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenPlayerHitAndShipSank() throws Exception {
        BattleFieldModel battleFieldModel = singlePlayerController.createRandomBattleField().getBody();
        assertNotNull(battleFieldModel);
        assertNotNull(battleFieldModel.getShips());
        assertEquals(battleFieldModel.getShips().size(), 10);
        Ship ship = battleFieldModel.getShips().get(9);
        List<Integer> coordinates = new ArrayList<>(ship.getCoordinates());
        List<Integer> spaceAround = new ArrayList<>(ship.getSpaceAround());
        assertEquals(coordinates.size(), 1);
        int point = coordinates.get(0);

        SinglePlayerGameModel model = new SinglePlayerGameModel();
        model.setBattleFieldModel(battleFieldModel);

        String modelJson = objectMapper.writeValueAsString(model);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/game/"+point)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        SinglePlayerGameModel actual =
                objectMapper.readValue(response.getContentAsString(), SinglePlayerGameModel.class);
        assertNotNull(actual);
        assertFalse(actual.isBotStatus());
        BattleFieldModel actualBattleFieldModel = actual.getBattleFieldModel();
        assertEquals(actualBattleFieldModel.getShips().size(), 9);
        assertFalse(actualBattleFieldModel.getShips().contains(ship));
        for(int i : coordinates) {
            assertEquals(actualBattleFieldModel.getBattleField()[i], 5);
        }
        for(int i : spaceAround) {
            assertEquals(actualBattleFieldModel.getBattleField()[i], 6);
        }
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenBotGetRandomHit() throws Exception {
        BattleFieldModel battleFieldModel = singlePlayerController.createRandomBattleField().getBody();
        assertNotNull(battleFieldModel);
        List<Integer> checkHits = new ArrayList<>();
        for(int i : battleFieldModel.getBattleField()) {
            if(i > 2) {
                checkHits.add(i);
            }
        }
        assertEquals(checkHits.size(), 0);
        Random random = new Random();

        SinglePlayerGameModel model = new SinglePlayerGameModel();
        model.setBattleFieldModel(battleFieldModel);
        model.setBotStatus(true);
        model.setBotLastHits(new ArrayList<>());

        String modelJson = objectMapper.writeValueAsString(model);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/game/"+random.nextInt(100))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        SinglePlayerGameModel actual =
                objectMapper.readValue(response.getContentAsString(), SinglePlayerGameModel.class);
        assertNotNull(actual);
        for(int i : actual.getBattleFieldModel().getBattleField()) {
            if(i > 2) {
                checkHits.add(i);
            }
        }
        assertEquals(checkHits.size(), 1);
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenBotGetNextHit() throws Exception {
        Random random = new Random();
        BattleFieldModel battleFieldModel = getBattleFieldModel();
        battleFieldModel.getBattleField()[70] = 4;
        assertEquals(battleFieldModel.getBattleField()[71], 1);
        SinglePlayerGameModel model = new SinglePlayerGameModel();
        model.setBattleFieldModel(battleFieldModel);
        model.setBotStatus(true);
        model.setBotLastHits(List.of(70));

        String modelJson = objectMapper.writeValueAsString(model);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/game/"+random.nextInt(100))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        SinglePlayerGameModel actual =
                objectMapper.readValue(response.getContentAsString(), SinglePlayerGameModel.class);
        assertNotNull(actual);
        assertEquals(actual.getBattleFieldModel().getBattleField()[71], 4);
        assertEquals(actual.getBotLastHits().size(), 2);
        assertEquals(actual.getBotLastHits().get(1), 71);
    }

    @Test
    void shouldReturnSinglePlayerGameModelWhenBotGetNextHitAndShipSank() throws Exception {
        Random random = new Random();
        BattleFieldModel battleFieldModel = getBattleFieldModel();
        battleFieldModel.getBattleField()[70] = 4;
        battleFieldModel.getBattleField()[71] = 4;
        List<Ship> ships = battleFieldModel.getShips();
        Ship ship = ships.get(1);
        assertEquals(ship.getCoordinates().size(), 3);
        List<Integer> coordinates = new ArrayList<>(ship.getCoordinates());
        List<Integer> spaceAround = new ArrayList<>(ship.getSpaceAround());

        SinglePlayerGameModel model = new SinglePlayerGameModel();
        model.setBattleFieldModel(battleFieldModel);
        model.setBotStatus(true);
        model.setBotLastHits(List.of(70,71));

        String modelJson = objectMapper.writeValueAsString(model);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/game/"+random.nextInt(100))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        SinglePlayerGameModel actual =
                objectMapper.readValue(response.getContentAsString(), SinglePlayerGameModel.class);
        assertNotNull(actual);
        BattleFieldModel actualBattleFieldModel = actual.getBattleFieldModel();
        assertEquals(actualBattleFieldModel.getShips().size(), 9);
        assertFalse(actualBattleFieldModel.getShips().contains(ship));
        for(int i : coordinates) {
            assertEquals(actualBattleFieldModel.getBattleField()[i], 5);
        }
        for(int i : spaceAround) {
            assertEquals(actualBattleFieldModel.getBattleField()[i], 6);
        }
    }

    private BattleFieldModel getBattleFieldModel() {
        List<Ship> ships = List.of(
                new Ship(Set.of(11,12,13,14), Set.of(0,1,2,3,4,5,10,15,20,21,22,23,24,25)),
                new Ship(Set.of(70,71,72), Set.of(60,61,62,63,73,80,81,82,83)),
                new Ship(Set.of(8,18,28), Set.of(7,9,17,19,27,29,37,38,39)),
                new Ship(Set.of(59,69), Set.of(48,49,58,68,78,79)),
                new Ship(Set.of(54,64), Set.of(43,44,45,53,55,63,65,73,74,75)),
                new Ship(Set.of(76,77), Set.of(65,66,67,68,75,78,85,86,87,88)),
                new Ship(Set.of(50), Set.of(40,41,51,60,61)),
                new Ship(Set.of(35), Set.of(24,25,26,34,36,44,45,46)),
                new Ship(Set.of(96), Set.of(85,86,87,95,97)),
                new Ship(Set.of(91), Set.of(80,81,82,90,92))
        );

        int[] battleField = new int[] {
                2, 2, 2, 2, 2, 2, 0, 2, 1, 2,
                2, 1, 1, 1, 1, 2, 0, 2, 1, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 1, 2,
                0, 0, 0, 0, 2, 1, 2, 2, 2, 2,
                2, 2, 0, 2, 2, 2, 2, 0, 2, 2,
                1, 2, 0, 2, 1, 2, 0, 0, 2, 1,
                2, 2, 2, 2, 1, 2, 2, 2, 2, 1,
                1, 1, 1, 2, 2, 2, 1, 1, 2, 2,
                2, 2, 2, 2, 0, 2, 2, 2, 2, 0,
                2, 1, 2, 0, 0, 2, 1, 2, 0, 0
        };

        BattleFieldModel model = new BattleFieldModel();
        model.setShips(ships);
        model.setBattleField(battleField);
        return model;
    }
}