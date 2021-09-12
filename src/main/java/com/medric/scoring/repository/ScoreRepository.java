package com.medric.scoring.repository;

import com.medric.scoring.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    boolean existsByPlayerId(Long playerId);
}
