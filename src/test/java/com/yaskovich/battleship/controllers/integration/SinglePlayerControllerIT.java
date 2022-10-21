package com.yaskovich.battleship.controllers.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaskovich.battleship.api.controllers.SinglePlayerController;
import com.yaskovich.battleship.models.BattleFieldModel;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
