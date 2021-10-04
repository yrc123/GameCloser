package com.yrc.gamecloserservice.service;

import java.util.List;
import java.util.Map;

import com.yrc.gamecloserservice.pojo.ProcessResultDTO;

public interface GameCloserSocketService {
    public void sendCloseGameMessage(String gameName, String hostName);

    List<String> listSockets();
    List<ProcessResultDTO> listResults();
}
