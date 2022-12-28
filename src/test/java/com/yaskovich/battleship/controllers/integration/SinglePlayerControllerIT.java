package com.yaskovich.battleship.controllers.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaskovich.battleship.api.controllers.SinglePlayerController;
import com.yaskovich.battleship.api.response.BaseResponse;
import com.yaskovich.battleship.api.response.GameModelUIResponse;
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

import java.util.UUID;

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
    void shouldReturnGameModelUITest() throws Exception {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        String modelJson = objectMapper.writeValueAsString(preparingModel);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/random_battlefield")
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
        assertEquals("Bot", actual.getGameModelUI().getEnemyModel().getPlayerName());
        assertEquals(actual.getGameModelUI().getActivePlayer(), actual.getGameModelUI().getPlayerModel().getPlayerId());

        preparingModel.setPlayerId(actual.getGameModelUI().getPlayerModel().getPlayerId());
        modelJson = objectMapper.writeValueAsString(preparingModel);
        response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/random_battlefield")
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
    void shouldDeleteGameModelTest() throws Exception {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        String modelJson = objectMapper.writeValueAsString(preparingModel);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/random_battlefield")
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

        UUID gameId = actual.getGameModelUI().getGameId();
        response =
                mockMvc.perform(get(SINGLE_PLAYER_ENDPOINT+"/game/"+gameId+"/delete")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);
        BaseResponse res = objectMapper.readValue(response.getContentAsString(), BaseResponse.class);
        assertNotNull(res);
        assertNotNull(res.getStatus());
        assertEquals(BaseResponse.Status.SUCCESS, res.getStatus());
    }

    @Test
    void shouldSaveGameTest() throws Exception {
        String expectedName = "Name";
        PreparingModel preparingModel = new PreparingModel(null, expectedName);
        String modelJson = objectMapper.writeValueAsString(preparingModel);
        MockHttpServletResponse response =
                mockMvc.perform(post(SINGLE_PLAYER_ENDPOINT+"/random_battlefield")
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

        UUID gameId = actual.getGameModelUI().getGameId();
        response =
                mockMvc.perform(get(SINGLE_PLAYER_ENDPOINT+"/game/"+gameId+"/save")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        assertNotNull(response);
        BaseResponse res = objectMapper.readValue(response.getContentAsString(), BaseResponse.class);
        assertNotNull(res);
        assertNotNull(res.getStatus());
        assertEquals(BaseResponse.Status.FAILURE, res.getStatus());
    }
}