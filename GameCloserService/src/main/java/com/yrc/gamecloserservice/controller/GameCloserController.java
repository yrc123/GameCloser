package com.yrc.gamecloserservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yrc.gamecloserservice.pojo.StopMessageDTO;
import com.yrc.gamecloserservice.pojo.ResponseDTO;
import com.yrc.gamecloserservice.service.GameCloserSocketService;
import com.yrc.gamecloserservice.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class GameCloserController {
    GameCloserSocketService gameCloserSocketService;

    @Autowired
    public GameCloserController(GameCloserSocketService gameCloserSocketService) {
        this.gameCloserSocketService = gameCloserSocketService;
    }

    @PostMapping("/devices/device/{hostName}")
    public ResponseDTO stopGameByHostName(@RequestBody StopMessageDTO device,
            @PathVariable("hostName") String hostname){
        if (!StringUtils.equals(hostname, device.getHostname())){
            return ResponseUtil.getFailResponse("different hostname");
        }
        Boolean result = gameCloserSocketService.sendCloseGameMessage(device.getGameName(),device.getHostname());
        if(Boolean.TRUE.equals(result)){
            return ResponseUtil.getSuccessResponse("success");
        }else {
            return ResponseUtil.getFailResponse("invalid hostname");
        }
    }
    @GetMapping("/devices")
    public ResponseDTO getSocketList(){
        Map<String, Object> data = new HashMap<>();
        data.put("deviceList", gameCloserSocketService.listSockets());
        return ResponseUtil.getSuccessResponse("sockets",data);
    }
    @GetMapping("/devices/result")
    public ResponseDTO getResultList(){
        Map<String, Object> data = new HashMap<>();
        data.put("resultList", gameCloserSocketService.listResults());
        return ResponseUtil.getSuccessResponse("results",data);
    }
}
