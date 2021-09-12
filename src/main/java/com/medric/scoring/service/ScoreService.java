package com.medric.scoring.service;

import com.medric.scoring.controller.dto.LeaderBoardDto;
import com.medric.scoring.entity.Score;

public interface ScoreService {

    Score saveScore(Score score);

    LeaderBoardDto retrieveLeaderboardForPlayer(Long playerId) throws Exception;
}
