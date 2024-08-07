package com.fitmate.oauth.dto.alarmDto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmServiceDto {
    private String username;
    private Integer contentId;
    private String title;
    private String content;

    public static FcmServiceDto of(String username, Integer contentId, String title, String content){
        FcmServiceDto dto = new FcmServiceDto();
        dto.username = username;
        dto.contentId = contentId;
        dto.title = title;
        dto.content = content;
        return dto;
    }
}
