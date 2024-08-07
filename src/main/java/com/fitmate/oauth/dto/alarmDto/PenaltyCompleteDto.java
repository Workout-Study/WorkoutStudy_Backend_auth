package com.fitmate.oauth.dto.alarmDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyCompleteDto {
    private long userId;
    private String message;
}