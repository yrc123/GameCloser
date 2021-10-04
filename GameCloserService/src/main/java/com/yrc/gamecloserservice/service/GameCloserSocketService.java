package com.yrc.gamecloserservice.service;

import java.util.List;

import com.yrc.gamecloserservice.pojo.DeviceDTO;
import com.yrc.gamecloserservice.pojo.ProcessResultDTO;

public interface GameCloserSocketService {
    public Boolean sendCloseGameMessage(String gameName, String hostName);

    List<DeviceDTO> listSockets();
    List<ProcessResultDTO> listResults();
}
