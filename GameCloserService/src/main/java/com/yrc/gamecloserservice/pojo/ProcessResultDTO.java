package com.yrc.gamecloserservice.pojo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProcessResultDTO {
    String ipAddress;
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

    public ProcessResultDTO(String ipAddress, Future<Integer> resultCode) {
        this.ipAddress = ipAddress;
        this.resultCode = resultCode;
    }
}
