package com.fitmate.oauth.vo.naver;

import lombok.Data;

@Data
public class NaverGetProfileVo {
    String resultCode;
    Response response;

    @Data
    public static class Response {
        private String id;
    }
}

/* 응답 예
{
  "resultcode": "00",
  "message": "success",
  "response": {
    "email": "openapi@naver.com",
    "nickname": "OpenAPI",
    "profile_image": "https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif",
    "age": "40-49",
    "gender": "F",
    "id": "32742776",
    "name": "오픈 API",
    "birthday": "10-01",
    "birthyear": "1900",
    "mobile": "010-0000-0000"
  }
}
 */