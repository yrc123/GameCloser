package com.yrc.gamecloserservice.pojo;

import com.fasterxml.jackson.annotation.JsonValue;

public class SendDTO {
    private Type type;
    private String guid;
    private Integer resultCode;
    private String gameName;
    public static SendDTO keepAliveObject(){
        return new SendDTO(Type.KEEY_ALIVE);
    }
    public enum Type{
        KEEY_ALIVE(1),
        EXEC(2),
        RESULT(3);
        int typeCode;
        Type(Integer typeCode){
            this.typeCode = typeCode;
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

    private SendDTO(Type type) {
        this.type = type;
    }
}
