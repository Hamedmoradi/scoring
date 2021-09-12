package com.medric.scoring.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Getter
@Setter
@Builder
public class ScoreResponseDto {

    private Long playerId;

    private Long score;

    private Long datetime;
}
