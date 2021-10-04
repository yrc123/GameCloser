package com.yrc.gamecloserservice.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor(){
        return new ThreadPoolExecutor(3, 8, 30, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
    }
}
