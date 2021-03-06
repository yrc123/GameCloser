package com.yrc.gamecloserservice.pojo;

import java.util.Map;


public class ResponseDTO {
    Integer errorCode;
    String message;
    Map<String,Object> data;

    public ResponseDTO(Integer errorCode, String message, Map<String, Object> data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
