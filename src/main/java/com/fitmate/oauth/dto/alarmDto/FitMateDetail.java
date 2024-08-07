package com.fitmate.oauth.dto.alarmDto;

import lombok.Data;

@Data
public class FitMateDetail {
    private Long fitMateId;
    private Long fitMateUserId;
    private String fitMateUserNickname;
    private String createdAt;
}
