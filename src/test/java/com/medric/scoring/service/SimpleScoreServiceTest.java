package com.medric.scoring.service;

import com.medric.scoring.controller.dto.LeaderBoardDto;
import com.medric.scoring.controller.dto.RecordDto;
import com.medric.scoring.entity.Score;
import com.medric.scoring.repository.ScoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
public class SimpleScoreServiceTest {

    @Autowired
    private ScoreService scoreService;

    @MockBean
    private ScoreRepository scoreRepositoryMocked;

    @Test
    public void retrieveLeaderboardForPlayer_InvalidPlayerId_ShouldThrowsException() {
        doReturn(false).when(scoreRepositoryMocked).existsByPlayerId(2L);

        assertThatExceptionOfType(Exception.class).as("Player not found")
                .isThrownBy(() -> scoreService.retrieveLeaderboardForPlayer(2L));
    }

    @Test
    public void retrieveLeaderboardForPlayer_ValidPlayerId_ShouldReturnResponse() throws Exception {
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

        doReturn(true).when(scoreRepositoryMocked).existsByPlayerId(2L);
        doReturn(scores).when(scoreRepositoryMocked).findAll();

        LeaderBoardDto actualResult = scoreService.retrieveLeaderboardForPlayer(2L);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    /**
     * Test suite configuration to create a service bean.
     */
    @TestConfiguration
    public static class ScoreServiceTestConfiguration {

        @Bean
        public ScoreService scoreService(ScoreRepository scoreRepository) {
            return new SimpleScoreService(scoreRepository);
        }
    }
}
