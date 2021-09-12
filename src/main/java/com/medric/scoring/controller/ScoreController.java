package com.medric.scoring.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.medric.scoring.controller.dto.LeaderBoardDto;
import com.medric.scoring.controller.dto.NewScoreDto;
import com.medric.scoring.controller.dto.ScoreResponseDto;
import com.medric.scoring.entity.Score;
import com.medric.scoring.service.ScoreService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("game/score")
@AllArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;

    @PostMapping
    public ResponseEntity<ScoreResponseDto> addScore(@Validated @RequestBody NewScoreDto newScoreDto) {
        log.info("Post request url:{},body:{}", "game/score", newScoreDto.toString());

        Score score = scoreService.saveScore(Score.builder().playerId(newScoreDto.getPlayerId())
                .value(newScoreDto.getScore()).build());

        return ResponseEntity.of(Optional.of(ScoreResponseDto.builder().score(score.getValue())
                .playerId(score.getPlayerId())
                .datetime(score.getDatetime().getEpochSecond()).build()));
    }

    @GetMapping
    public ResponseEntity<LeaderBoardDto> getLeaderBoard(@RequestParam(value = "playerid") Long playerId)
            throws Exception {
        log.info("Get request url:{},playerId:{}", "game/score", playerId);

        LeaderBoardDto leaderBoardDto = scoreService.retrieveLeaderboardForPlayer(playerId);

        return ResponseEntity.of(Optional.of(leaderBoardDto));
    }
}
