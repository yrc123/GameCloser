package com.yrc.gamecloserservice.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class SendDTO {
    private Type type;
    private String guid;
    private Integer resultCode;
    private String gameName;
    private String hostname;
    public static SendDTO keepAliveObject(String hostname){
        return new SendDTO(Type.KEEY_ALIVE, hostname);
    }
    public SendDTO(){}
    public static SendDTO closeGameObject(String guid, String hostname, String gameName){
        return new SendDTO(Type.EXEC, guid, gameName, hostname);
    }
    public enum Type{
        UNKNOW(0),
        KEEY_ALIVE(1),
        EXEC(2),
        RESULT(3);
        int typeCode;
        private static Map<Integer, Type> typeMap = new HashMap<>();
        static {
            typeMap.put(UNKNOW.getTypeCode(), UNKNOW);
            typeMap.put(KEEY_ALIVE.getTypeCode(),KEEY_ALIVE);
            typeMap.put(EXEC.getTypeCode(), EXEC);
            typeMap.put(RESULT.getTypeCode(), RESULT);
        }
        Type(Integer typeCode){
            this.typeCode = typeCode;
        }

        @JsonCreator
        public static Type forValue(Integer typeCode){
            return typeMap.getOrDefault(typeCode,Type.UNKNOW);
        }
        @Override
        public String toString() {
            return String.valueOf(typeCode);
        }

        @JsonValue
        public int getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(int typeCode) {
            this.typeCode = typeCode;
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    private SendDTO(Type type,String hostname) {
        this.type = type;
        this.hostname = hostname;
    }

    public SendDTO(Type type, String guid, String gameName, String hostname) {
        this.type = type;
        this.guid = guid;
        this.gameName = gameName;
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
