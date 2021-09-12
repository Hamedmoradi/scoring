package com.medric.scoring.controller.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class RecordDto {
    private Long playerId;
    private Long score;
    private int rank;
}
