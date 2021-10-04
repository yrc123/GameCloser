package com.yrc.gamecloserservice.pojo;

public class StopMessageDTO {
    private String hostname;
    private String gameName;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
