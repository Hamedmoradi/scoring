package com.medric.scoring.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
public class NewScoreDto {

    @NotNull(message = "player id is null")
    private Long playerId;

    @Range(min= 0, max= 100)
    private Long score;

    private Instant datetime;
}
