package com.yrc.gamecloserservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameCloserSocketConfig {
    @Value("${socket.port}")
    private String port;

    public Integer getPort() {
        return Integer.parseInt(port);
    }

    public void setPort(Integer port) {
        this.port = port.toString();
    }
}
