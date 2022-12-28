package com.yaskovich.battleship.controllers.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaskovich.battleship.api.controllers.MultiplayerController;
import com.yaskovich.battleship.api.response.BaseResponse;
import com.yaskovich.battleship.api.response.GameModelUIResponse;
import com.yaskovich.battleship.models.FreeGame;
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

        GameModelUIResponse actual = objectMapper.readValue(response.getContentAsString(), GameModelUIResponse.class);
        assertNotNull(actual);
        assertNotNull(actual.getStatus());
        assertEquals(BaseResponse.Status.SUCCESS, actual.getStatus());
        assertNotNull(actual.getGameModelUI());
        assertEquals(expectedName, actual.getGameModelUI().getPlayerModel().getPlayerName());
        assertEquals(10, actual.getGameModelUI().getPlayerModel().getSizeOfShips());
        assertEquals("Unknown player", actual.getGameModelUI().getEnemyModel().getPlayerName());
        assertEquals(actual.getGameModelUI().getActivePlayer(), actual.getGameModelUI().getPlayerModel().getPlayerId());

        preparingModel.setPlayerId(actual.getGameModelUI().getPlayerModel().getPlayerId());
        modelJson = objectMapper.writeValueAsString(preparingModel);
        response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/random_battlefield")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        GameModelUIResponse changed = objectMapper.readValue(response.getContentAsString(), GameModelUIResponse.class);
        assertNotNull(changed);
        assertNotNull(changed.getGameModelUI().getPlayerModel());
        assertNotNull(changed.getGameModelUI().getEnemyModel());

        assertEquals(actual.getGameModelUI().getGameId(), changed.getGameModelUI().getGameId());
        assertEquals(actual.getGameModelUI().getEnemyModel(), changed.getGameModelUI().getEnemyModel());
        assertEquals(actual.getGameModelUI().getPlayerModel().getPlayerId(),
                changed.getGameModelUI().getPlayerModel().getPlayerId());
        assertEquals(actual.getGameModelUI().getPlayerModel().getPlayerName(),
                changed.getGameModelUI().getPlayerModel().getPlayerName());
        assertNotEquals(actual.getGameModelUI().getPlayerModel().getBattleField(),
                changed.getGameModelUI().getPlayerModel().getBattleField());
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

        GameModelUIResponse actual = objectMapper.readValue(response.getContentAsString(), GameModelUIResponse.class);
        assertNotNull(actual);
        assertNotNull(actual.getStatus());
        assertEquals(BaseResponse.Status.SUCCESS, actual.getStatus());
        assertNotNull(actual.getGameModelUI());

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
        GameModelUIResponse selected = objectMapper.readValue(response.getContentAsString(), GameModelUIResponse.class);
        assertNotNull(selected);
        assertNotNull(selected.getStatus());
        assertEquals(BaseResponse.Status.SUCCESS, selected.getStatus());
        assertNotNull(selected.getGameModelUI());

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
        GameModelUIResponse joined = objectMapper.readValue(response.getContentAsString(), GameModelUIResponse.class);
        assertNotNull(joined);
        assertNotNull(joined.getStatus());
        assertEquals(BaseResponse.Status.SUCCESS, joined.getStatus());
        assertNotNull(joined.getGameModelUI());

        UUID gameId = selected.getGameModelUI().getGameId();
        modelJson = objectMapper.writeValueAsString(joined.getGameModelUI());
        response =
                mockMvc.perform(post(MULTIPLAYER_ENDPOINT+"/game/"+gameId+"/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(modelJson)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);

        GameModelUIResponse result = objectMapper.readValue(response.getContentAsString(), GameModelUIResponse.class);
        assertNotNull(result);
        assertNotNull(result.getStatus());
        assertEquals(BaseResponse.Status.SUCCESS, result.getStatus());
        assertNotNull(result.getGameModelUI());
        assertEquals(selected.getGameModelUI().getGameId(), result.getGameModelUI().getGameId());
        assertEquals(selected.getGameModelUI().getPlayerModel().getPlayerId(),
                result.getGameModelUI().getEnemyModel().getPlayerId());
        assertEquals(joined.getGameModelUI().getPlayerModel().getPlayerId(),
                result.getGameModelUI().getPlayerModel().getPlayerId());
        assertEquals(result.getGameModelUI().getEnemyModel().getPlayerId(), result.getGameModelUI().getActivePlayer());
    }
}
