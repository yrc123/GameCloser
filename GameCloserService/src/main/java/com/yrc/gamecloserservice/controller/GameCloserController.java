package com.yrc.gamecloserservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.yrc.gamecloserservice.pojo.DeviceDTO;
import com.yrc.gamecloserservice.pojo.ResponseDTO;
import com.yrc.gamecloserservice.service.GameCloserSocketService;
import com.yrc.gamecloserservice.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class GameCloserController {
    @Autowired
    GameCloserSocketService gameCloserSocketService;
    @PostMapping("/devices/device/{hostName}")
    public ResponseDTO stopGameByHostName(@RequestBody DeviceDTO device,
            @PathVariable("hostName") String hostname){
        if (!StringUtils.equals(hostname, device.getHostname())){
            return ResponseUtil.getFailResponse("different hostname");
        }
        gameCloserSocketService.sendCloseGameMessage(device.getGameName(),device.getHostname());
        return ResponseUtil.getSuccessResponse("success");
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
