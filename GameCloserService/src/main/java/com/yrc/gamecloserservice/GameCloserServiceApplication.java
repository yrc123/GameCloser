package com.yrc.gamecloserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GameCloserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameCloserServiceApplication.class, args);
    }

}
