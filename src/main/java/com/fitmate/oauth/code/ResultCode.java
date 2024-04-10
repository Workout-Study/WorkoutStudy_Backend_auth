package com.fitmate.oauth.code;

public enum ResultCode {
    SUCCESS("200"),
    FAIL("500"),
    ;

    final String code;

    ResultCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return this.code;
    }
}
