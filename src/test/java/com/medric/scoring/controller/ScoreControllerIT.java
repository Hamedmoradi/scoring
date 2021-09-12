package com.medric.scoring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medric.scoring.controller.dto.LeaderBoardDto;
import com.medric.scoring.controller.dto.NewScoreDto;
import com.medric.scoring.controller.dto.RecordDto;
import com.medric.scoring.entity.Score;
import com.medric.scoring.repository.ScoreRepository;
import com.medric.scoring.service.ScoreService;
import com.medric.scoring.service.SimpleScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ScoreController.class)
@AutoConfigureDataJpa
public class ScoreControllerIT {

    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private ScoreService scoreService;
    @MockBean
    private ScoreRepository scoreRepositoryMocked;

    @Test
    public void addScore_InvalidScoreValue_ReturnError() throws Exception {
        mockMvc.perform(post("/game/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(NewScoreDto.builder().playerId(1L).score(2000L).build())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.[0].msg", is("must be between 0 and 100")))
                .andExpect(jsonPath("$.[0].data", is("2000")));
    }

    @Test
    public void getLeaderBoard_InvalidPlayerId_ReturnError() throws Exception {
        doReturn(false).when(scoreRepositoryMocked).existsByPlayerId(12L);
        mockMvc.perform(get("/game/score?playerid=12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg", is("Player not found")));
    }

    @Test
    public void getLeaderBoard_ValidPlayerId_ReturnLeaderBoard() throws Exception {
        doReturn(true).when(scoreRepositoryMocked).existsByPlayerId(2L);

        List<Score> scores =
                Arrays.asList(Score.builder().id(1L).playerId(1L).value(100L).build(),
                        Score.builder().id(2L).playerId(2L).value(50L).build(),
                        Score.builder().id(3L).playerId(2L).value(50L).build(),
                        Score.builder().id(4L).playerId(2L).value(50L).build(),
                        Score.builder().id(5L).playerId(3L).value(125L).build());

        List<RecordDto> expectedRecords = Arrays.asList(RecordDto.builder().playerId(2L).score(150L).rank(1).build(),
                RecordDto.builder().playerId(3L).score(125L).rank(2).build(),
                RecordDto.builder().playerId(1L).score(100L).rank(3).build());
        LeaderBoardDto expectedResult = LeaderBoardDto.builder().playerId(2L).score(150L)
                .rank(1).records(expectedRecords).build();

        doReturn(scores).when(scoreRepositoryMocked).findAll();

        mockMvc.perform(get("/game/score?playerid=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is(2)))
                .andExpect(jsonPath("$.score", is(expectedResult.getScore().intValue())))
                .andExpect(jsonPath("$.rank", is(expectedResult.getRank())))
                .andExpect(jsonPath("$.records", hasSize(expectedResult.getRecords().size())))
                .andExpect(jsonPath("$.records[2].playerId", is(expectedResult.getRecords().get(2).getPlayerId().intValue())))
                .andExpect(jsonPath("$.records[2].score", is(expectedResult.getRecords().get(2).getScore().intValue())))
                .andExpect(jsonPath("$.records[2].rank", is(expectedResult.getRecords().get(2).getRank())));

    }

    /**
     * @param object The object to convert to json
     * @return The json representation of the given {@code pojo}
     */
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert to json", e);
        }
    }

    /**
     * Test suite configuration to create a service bean.
     */
    @TestConfiguration
    public static class ScoreControllerTestConfiguration {

        @Bean
        public ScoreService scoreService(ScoreRepository scoreRepository) {
            return new SimpleScoreService(scoreRepository);
        }
    }
}
