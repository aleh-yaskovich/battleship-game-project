package com.yaskovich.battleship.controllers.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaskovich.battleship.api.controllers.MultiplayerController;
import com.yaskovich.battleship.models.FreeGame;
import com.yaskovich.battleship.models.GameModelUI;
import com.yaskovich.battleship.models.PreparingModel;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class MultiplayerControllerIT {

    public static final String MULTIPLAYER_ENDPOINT = "/multiplayer";
    @Autowired
    private MultiplayerController multiplayerController;
    @Autowired
    protected ObjectMapper objectMapper;
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = standaloneSetup(multiplayerController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnGameModelUITest() throws Exception {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        String modelJson = objectMapper.writeValueAsString(preparingModel);
        MockHttpServletResponse response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/random_battlefield")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        GameModelUI newModel = objectMapper.readValue(response.getContentAsString(), GameModelUI.class);
        assertNotNull(newModel);
        assertNotNull(newModel.getPlayerModel());
        assertNotNull(newModel.getEnemyModel());
        assertEquals(newModel.getPlayerModel().getPlayerName(), expectedName);
        assertEquals(newModel.getPlayerModel().getSizeOfShips(), 10);
        assertEquals(newModel.getEnemyModel().getPlayerName(), "Unknown player");
        assertEquals(newModel.getActivePlayer(), newModel.getPlayerModel().getPlayerId());

        preparingModel.setPlayerId(newModel.getPlayerModel().getPlayerId());
        modelJson = objectMapper.writeValueAsString(preparingModel);
        response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/random_battlefield")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        GameModelUI changedModel = objectMapper.readValue(response.getContentAsString(), GameModelUI.class);
        assertNotNull(changedModel);
        assertNotNull(changedModel.getPlayerModel());
        assertNotNull(changedModel.getEnemyModel());

        assertEquals(newModel.getGameId(), changedModel.getGameId());
        assertEquals(newModel.getEnemyModel(), changedModel.getEnemyModel());
        assertEquals(newModel.getPlayerModel().getPlayerId(), changedModel.getPlayerModel().getPlayerId());
        assertEquals(newModel.getPlayerModel().getPlayerName(), changedModel.getPlayerModel().getPlayerName());
        assertNotEquals(newModel.getPlayerModel().getBattleField(), changedModel.getPlayerModel().getBattleField());
    }

    @Test
    void shouldReturnFreeGamesList() throws Exception {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        String modelJson = objectMapper.writeValueAsString(preparingModel);
        MockHttpServletResponse response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/random_battlefield")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);
        GameModelUI  gameModelUI = objectMapper.readValue(response.getContentAsString(), GameModelUI.class);
        assertNotNull(gameModelUI);
        assertNotNull(gameModelUI.getGameId());
        assertNotNull(gameModelUI.getPlayerModel());
        assertNotNull(gameModelUI.getPlayerModel().getPlayerName());

        UUID withoutId = UUID.randomUUID();
        response =
                mockMvc.perform(get(MULTIPLAYER_ENDPOINT+"/free_games?withoutId="+withoutId)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        List<FreeGame> freeGames = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});
        assertNotNull(freeGames);
        assertTrue(freeGames.size() > 0);
    }

    @Test
    void shouldJoinToMultiplayerGame() throws Exception {
        PreparingModel preparingModel = new PreparingModel(null, "Player1");
        String modelJson = objectMapper.writeValueAsString(preparingModel);
        MockHttpServletResponse response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/random_battlefield")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);
        GameModelUI  selectedModel = objectMapper.readValue(response.getContentAsString(), GameModelUI.class);

        preparingModel = new PreparingModel(null, "Player2");
        modelJson = objectMapper.writeValueAsString(preparingModel);
        response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/random_battlefield")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);
        GameModelUI  joinedModel = objectMapper.readValue(response.getContentAsString(), GameModelUI.class);

        UUID gameId = selectedModel.getGameId();
        modelJson = objectMapper.writeValueAsString(joinedModel);
        response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/game/"+gameId+"/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        GameModelUI result = objectMapper.readValue(response.getContentAsString(), GameModelUI.class);
        assertNotNull(result);
        assertEquals(selectedModel.getGameId(), result.getGameId());
        assertEquals(selectedModel.getPlayerModel().getPlayerId(), result.getEnemyModel().getPlayerId());
        assertEquals(joinedModel.getPlayerModel().getPlayerId(), result.getPlayerModel().getPlayerId());
        assertEquals(result.getEnemyModel().getPlayerId(), result.getActivePlayer());
    }
}
