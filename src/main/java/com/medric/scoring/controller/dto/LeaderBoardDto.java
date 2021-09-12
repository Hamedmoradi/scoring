package com.medric.scoring.controller.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderBoardDto {

    private Long playerId;
    private Long score;
    private Integer rank;
    private List<RecordDto> records;
}
