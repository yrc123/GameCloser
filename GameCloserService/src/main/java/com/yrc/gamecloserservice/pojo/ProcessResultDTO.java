package com.yrc.gamecloserservice.pojo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProcessResultDTO {
    String ipAddress;
    String gameName;
    Future<Integer> resultCode;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getResultCode() {
        try {
            return resultCode.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setResultCode(Future<Integer> resultCode) {
        this.resultCode = resultCode;
    }

    public ProcessResultDTO() {
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public ProcessResultDTO(String ipAddress, String gameName, Future<Integer> resultCode) {
        this.ipAddress = ipAddress;
        this.gameName = gameName;
        this.resultCode = resultCode;
    }
}
