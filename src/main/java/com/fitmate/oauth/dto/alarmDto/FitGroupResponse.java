package com.fitmate.oauth.dto.alarmDto;

import lombok.Data;

import java.util.List;

@Data
public class FitGroupResponse {
    private Long fitGroupId;
    private FitLeaderDetail fitLeaderDetail;
    private List<FitMateDetail> fitMateDetails;
}
