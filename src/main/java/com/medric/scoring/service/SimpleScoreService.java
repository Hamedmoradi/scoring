package com.medric.scoring.service;

import com.medric.scoring.controller.dto.LeaderBoardDto;
import com.medric.scoring.controller.dto.RecordDto;
import com.medric.scoring.entity.Score;
import com.medric.scoring.repository.ScoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
@Service
public class SimpleScoreService implements ScoreService {

    private final ScoreRepository scoreRepository;

    @Override
    public Score saveScore(Score score) {
        return scoreRepository.save(score);
    }

    @Override
    public LeaderBoardDto retrieveLeaderboardForPlayer(Long playerId) throws Exception {
        if (!scoreRepository.existsByPlayerId(playerId))
            throw new Exception("Player not found");

        Map<Long, List<Score>> groupedScores = scoreRepository.findAll().stream()
                .collect(groupingBy(Score::getPlayerId));

        List<RecordDto> records = new ArrayList<>();
        groupedScores.keySet().forEach(id -> {
            List<Score> scores = groupedScores.get(id);
            AtomicReference<Long> sum = new AtomicReference<>(0l);
            scores.forEach(score -> sum.set(sum.get() + score.getValue()));
            records.add(RecordDto.builder().playerId(id).score(sum.get()).rank(0).build());
        });

        List<RecordDto> sortedRecords = records.stream()
                .sorted(Comparator.comparing(RecordDto::getScore).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < sortedRecords.size(); i++) {
            sortedRecords.get(i).setRank(i + 1);
        }

        RecordDto selectedPlayerRecord = sortedRecords.stream()
                .filter(recordDto -> recordDto.getPlayerId().equals(playerId)).findAny()
                .orElseThrow(() -> new Exception("Player not found"));

        return LeaderBoardDto.builder().playerId(playerId).rank(selectedPlayerRecord.getRank())
                .score(selectedPlayerRecord.getScore()).records(sortedRecords).build();
    }
}
