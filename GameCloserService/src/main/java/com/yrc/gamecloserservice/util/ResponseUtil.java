package com.yrc.gamecloserservice.util;

import java.util.Map;

import com.yrc.gamecloserservice.pojo.ResponseDTO;

public class ResponseUtil {
    public static ResponseDTO getSuccessResponse(String message, Map<String,Object> data){
        ResponseDTO responseDTO = new ResponseDTO(0,message,data);
        return responseDTO;
    }
    public static ResponseDTO getFailResponse(String message, Map<String,Object> data){
        ResponseDTO responseDTO = new ResponseDTO(-1,message,data);
        return  responseDTO;
    }
}
