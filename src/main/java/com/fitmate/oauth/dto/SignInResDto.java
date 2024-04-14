package com.fitmate.oauth.dto;

import com.fitmate.oauth.code.ResultCode;
import lombok.Getter;

@Getter
public class SignInResDto {
    private final ResultCode resultCode;

    private SignInResDto(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public static SignInResDto result(ResultCode resultCode) {
        return new SignInResDto(resultCode);
    }

    public String getResultCode() {
        return resultCode.getCode();
    }
}
