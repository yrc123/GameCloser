package com.yrc.gamecloserservice.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yrc.gamecloserservice.pojo.ResponseDTO;
import com.yrc.gamecloserservice.service.GameCloserSocketService;
import com.yrc.gamecloserservice.util.ResponseUtil;

@RestController
@RequestMapping("/api/game")
public class GameCloserController {
    @Autowired
    GameCloserSocketService gameCloserSocketService;
    @GetMapping("/{gameName}")
    public ResponseDTO test(@PathVariable("gameName") String gameName){
        gameCloserSocketService.sendCloseGameMessage(gameName);
        return ResponseUtil.getSuccessResponse("success",new HashMap<>());
    }
}
