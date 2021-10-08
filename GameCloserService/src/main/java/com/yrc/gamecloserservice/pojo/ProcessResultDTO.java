package com.yrc.gamecloserservice.pojo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProcessResultDTO {
    String guid;
    String hostname;
    String gameName;
    Integer resultCode;
    LocalDateTime createdTime;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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

    public ProcessResultDTO() {
        this.createdTime=LocalDateTime.now();
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getCreatedTime() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(createdTime);
    }
    public LocalDateTime getLocalDateTime(){
        return createdTime;
    }

    @Override
    public String toString() {
        return "ProcessResultDTO{" +
                "guid='" + guid + '\'' +
                ", hostname='" + hostname + '\'' +
                ", gameName='" + gameName + '\'' +
                ", resultCode=" + resultCode +
                ", createdTime=" + createdTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ProcessResultDTO resultDTO = (ProcessResultDTO) o;
        return Objects.equals(guid, resultDTO.guid) && Objects.equals(hostname, resultDTO.hostname) && Objects.equals(gameName, resultDTO.gameName) && Objects.equals(resultCode, resultDTO.resultCode) && Objects.equals(createdTime, resultDTO.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, hostname, gameName, resultCode, createdTime);
    }
}
